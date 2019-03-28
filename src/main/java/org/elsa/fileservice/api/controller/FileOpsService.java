package org.elsa.fileservice.api.controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.elsa.fileservice.api.request.CheckMd5;
import org.elsa.fileservice.api.response.GeneralResult;
import org.elsa.fileservice.api.response.ListResult;
import org.elsa.fileservice.api.response.adapter.CheckMd5Result;
import org.elsa.fileservice.api.response.adapter.FileSavedResult;
import org.elsa.fileservice.common.exception.NoteException;
import org.elsa.fileservice.common.utils.Encrypt;
import org.elsa.fileservice.common.utils.Jsons;
import org.elsa.fileservice.core.entity.FileSystem;
import org.elsa.fileservice.core.entity.UploadCert;
import org.elsa.fileservice.core.entity.Whitelist;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author valor
 * @date 2019-03-27 01:03
 */
@Service
public class FileOpsService extends FileOpsController {

    @Override
    public ListResult<CheckMd5Result> checkMd5() {

        // 获取前端传来的md5
        String json = super.request.getParameter("md5-check");
        if (StringUtils.isBlank(json)) {
            throw new NoteException("Error params.");
        }

        List<CheckMd5> checkMd5 = Jsons.parseArray(json, CheckMd5.class);
        if (null == checkMd5 || checkMd5.isEmpty()) {
            throw new NoteException("Blank params.");
        }

        List<CheckMd5Result> list = null;
        // 对md5值进行校验
        for (CheckMd5 e : checkMd5) {
            if (StringUtils.isBlank(e.getFlag())) {
                throw new NoteException("Blank flag.");
            }
            if (StringUtils.isBlank(e.getMd5())) {
                throw new NoteException("Blank MD5.");
            }

            if (null == list) {
                list = new ArrayList<>(checkMd5.size());
            }

            CheckMd5Result checkMd5Result = new CheckMd5Result();
            checkMd5Result.setFlag(e.getFlag());
            FileSystem checked = super.generalDaoHelper.quickQueryOne(FileSystem.class, "fileMd5", e.getMd5());
            if (null != checked) {
                // 存在md5 认为之前已上传
                checkMd5Result.setUploaded(true);
                checkMd5Result.setInfo(checked);
            } else {
                // 不存在md5 则颁发上传凭证
                long time = System.currentTimeMillis();

                UploadCert cert = super.certMapper.getTimeoutCertOne(time, super.uploadConf.getTimeout());
                if (null == cert) {
                    cert = new UploadCert();
                }
                cert.setCertificate(Encrypt.md5HexString(time + RandomStringUtils.randomAlphanumeric(10)));
                cert.setUsed(false);
                cert.setTime(time);
                super.generalDaoHelper.save(cert);

                checkMd5Result.setUploaded(false);
                checkMd5Result.setCert(cert);
            }

            list.add(checkMd5Result);
        }

        return new ListResult<CheckMd5Result>().setList(list);
    }

    @Override
    public GeneralResult<FileSavedResult> upload() throws Exception {
        // 判断content-type 是否为 form-data
        boolean f = ServletFileUpload.isMultipartContent(super.request);
        if (!f) {
            throw new NoteException("Please use 'form-data' to transfer files.");
        }

        ServletFileUpload upload = super.uploadCache.getUpload();
        List<FileItem> files = upload.parseRequest(super.request);
        if (null == files || files.isEmpty()) {
            throw new NoteException("No file(s) existed.");
        }

        // 记录文件保存结果
        int capacity = files.size() * 4 / 3 + 1;
        Map<String, String> saved = null;
        Map<String, String> failed = null;

        long time = System.currentTimeMillis();

        // 保存上传的文件
        for (FileItem file : files) {
            try {
                if (StringUtils.isBlank(file.getName()) || 60 < file.getName().length()) {
                    throw new NoteException("File name is blank or more than 60 characters.");
                }
                if (StringUtils.contains(file.getName(), "%00")) {
                    throw new NoteException("Block this file. Unsupported file name.");
                }

                // 判断文件的上传凭证是否可用
                UploadCert cert = super.generalDaoHelper.quickQueryOne(UploadCert.class, "certificate", file.getFieldName());
                if (null == cert || cert.getUsed()) {
                    throw new NoteException("Error upload certificate.");
                }
                if (time - cert.getTime() > super.uploadConf.getTimeout()) {
                    throw new NoteException("Timeout upload certificate.");
                }

                Whitelist whitelist = super.generalDaoHelper.quickQueryOne(Whitelist.class, "contentType", file.getContentType());
                if (null == whitelist) {
                    throw new NoteException("Block this file. Content-type[" + file.getContentType() + "] not in the whitelist.");
                }

                // todo 判断图片文件是否带有js

                // 计算文件md5
                String fileMd5 = Encrypt.md5HexString(file.getInputStream());
                FileSystem checked = super.generalDaoHelper.quickQueryOne(FileSystem.class, "fileMd5", fileMd5);
                if (null != checked) {
                    // 文件秒传
                    if (null == saved) {
                        saved = new HashMap<>(capacity);
                    }
                    saved.put(file.getFieldName(), checked.getSavedFilename());
                    continue;
                }

                // 如果没有抛出异常 则文件后缀名取白名单中的后缀名
                String saveName = time + "-" + Encrypt.md5HexString(time + file.getName()) + "." + whitelist.getExt();
                file.write(new File(super.uploadConf.getFileDir() + saveName));

                if (null == saved) {
                    saved = new HashMap<>(capacity);
                }
                saved.put(file.getFieldName(), saveName);

                checked = new FileSystem();
                checked.setFileMd5(fileMd5);
                checked.setSavedFilename(saveName);
                checked.setOriginalFilename(file.getName());
                checked.setEntry(time);
                checked.setSize(file.getSize());
                checked.setService(time);
                super.generalDaoHelper.save(checked);

            } catch (Exception e) {
                if (null == failed) {
                    failed = new HashMap<>(capacity);
                }
                failed.put(file.getFieldName(), e.getMessage());
            }
        }

        FileSavedResult result = new FileSavedResult();
        result.setSaved(saved);
        result.setFailed(failed);
        return new GeneralResult<FileSavedResult>().setValue(result);
    }

    @Override
    public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new NoteException("Blank of file name.");
        }

        FileSystem fileSystem = super.generalDaoHelper.quickQueryOne(FileSystem.class, "savedFilename", fileName);
        if (null == fileSystem) {
            throw new NoteException("No such file.");
        }

        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        Whitelist whitelist = super.generalDaoHelper.quickQueryOne(Whitelist.class, "ext", ext);
        if (null == whitelist) {
            throw new NoteException("Not in the whitelist.");
        }

        if (whitelist.getRange()) {
            // 如果需要断点续传 如视频
            String range = super.request.getHeader("range");
            if (StringUtils.isBlank(range) || !StringUtils.startsWith(range, "bytes=")) {
                throw new NoteException("Required field in request headers - Range. \nTemplate - 'Range: bytes=0-1;'");
            }

            // 处理断点字段
            String[] values = StringUtils.split(StringUtils.split(range, "=")[1], "-");
            if (values.length == 0) {
                throw new NoteException("Plz check 'Range' in headers. \nTemplate - 'Range: bytes=0-1;'");
            }

            // 32Mb 对应的 byte大小
            int mb32 = super.uploadConf.getGetBuffer();

            int start, end = 0;
            start = Integer.parseInt(values[0]);
            if (values.length > 1) {
                end = Integer.parseInt(values[1]);
            } else {
                // 如果文件大于32Mb 则只传32Mb
                long size = fileSystem.getSize() - start;
                if (size > mb32) {
                    end = start + mb32 - 1;
                } else {
                    end = Math.toIntExact(size) - 1;
                }
            }

            if (start >= end) {
                throw new NoteException("Plz check 'Range' in headers. \nTemplate - 'Range: bytes=0-1;'");
            }
            int reqSize = end - start + 1;
            // 每次只允许32Mb大小的流进行传输
            if (reqSize > mb32) {
                throw new NoteException("Plz check 'Range' in headers. \nrangeStart - rangeEnd <= 32Mb.");
            }

            // 清空response
            response.reset();
            response.setContentType(whitelist.getContentType());
            // 断点续传的方式来返回视频数据 206
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", reqSize + "");
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileSystem.getSize());
            this.flush(super.uploadConf.getFileDir() + fileName, response, start, reqSize, fileSystem);


        } else {
            // 不需要断点续传 如图片

            // 清空response
            response.reset();
            response.setContentType(whitelist.getContentType());
            this.flush(super.uploadConf.getFileDir() + fileName, response, null, Math.toIntExact(fileSystem.getSize()), fileSystem);

        }


//        InputStream fis = null;
//        try {
//            // 读取文件流
//            fis = new FileInputStream(super.config.getFileDir() + fileName);
//            byte[] bytes = new byte[fis.available()];
//            int i = fis.read(bytes);
//
//            if (-1 == i) {
//                throw new NoteException("Error reading file to bytes.");
//            }
//
//            // 清空response
//            response.reset();
//            response.setContentType(whitelist.getContentType());
//
//            OutputStream out = response.getOutputStream();
//            out.write(bytes);
//            out.flush();
//
//        } catch (Exception e) {
//            throw new NoteException(e);
//        } finally {
//            if (null != fis) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

    }

    /**
     * 读取文件 并返回流
     */
    private void flush(String filePath, HttpServletResponse response, Integer seek, int reqSize, FileSystem fileSystem) {

        OutputStream out = null;
        RandomAccessFile file = null;
        try {
            // 处理文件 返回流
            out = response.getOutputStream();

            // 只读模式
            file = new RandomAccessFile(filePath, "r");
            if (null != seek) {
                file.seek(seek);
            }

            // 每次读取的大小
            byte[] buffer = new byte[super.uploadConf.getReadBytes()];

            int needSize = reqSize;
            while (needSize > 0) {
                int len = file.read(buffer);
                if (needSize < buffer.length) {
                    out.write(buffer, 0, needSize);
                } else {
                    out.write(buffer, 0, len);
                    if (len < buffer.length) {
                        break;
                    }
                }
                needSize -= buffer.length;
            }

            fileSystem.setNumber(fileSystem.getNumber() + 1);
            fileSystem.setService(System.currentTimeMillis());
            super.generalDaoHelper.save(fileSystem);
        } catch (Exception e) {
            throw new NoteException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (file != null) {
                    file.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
