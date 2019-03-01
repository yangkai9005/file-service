package org.elsa.filemanager.api.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.elsa.filemanager.api.response.GeneralResult;
import org.elsa.filemanager.api.response.adapter.FileSavedResult;
import org.elsa.filemanager.common.exception.NoteException;
import org.elsa.filemanager.common.utils.Encrypt;
import org.elsa.filemanager.common.utils.Files;
import org.elsa.filemanager.core.entity.FileSystem;
import org.elsa.filemanager.core.entity.Whitelist;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author valor
 * @date 2018/11/21 14:34
 */
@RestController
public class FileOpsController extends BaseController {

    @Override
    public GeneralResult<FileSavedResult> upload() {
        // 判断content-type 是否为 form-data
        if (!StringUtils.contains(super.request.getContentType(), "form-data")) {
            throw new NoteException("Please use 'form-data' to pass files.");
        }

        // 使用MultipartResolver 转换请求为 MultipartHttpServletRequest
        MultipartResolver resolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest mRequest = resolver.resolveMultipart(super.request);
        // 获取上传文件的map 可以对多文件上传进行处理
        Map<String, MultipartFile> mapFiles = mRequest.getFileMap();

        if (null == mapFiles || 0 == mapFiles.size()) {
            throw new NoteException("No file(s) existed.");
        }

        // 记录文件保存结果
        int capacity = mapFiles.size() * 4 / 3 + 1;
        Map<String, String> saved = new HashMap<>(capacity);
        Map<String, String> failed = new HashMap<>(8);

        // 保存的文件相关参数
        String fileSavedName;
        FileSystem fileSystem;
        MultipartFile file;
        // 对上传对文件循环保存
        for (Map.Entry<String, MultipartFile> entry : mapFiles.entrySet()) {
            file = entry.getValue();

            // file rule
            if (null == file.getOriginalFilename() || 60 < file.getOriginalFilename().length()) {
                failed.put(entry.getKey(), "File name is null or more than 60 characters.");
                continue;
            }

            if (StringUtils.contains(file.getOriginalFilename(), "%00")) {
                failed.put(entry.getKey(), "Block this file. Unsupported file name.");
                continue;
            }

            // 判断文件头是否在枚举类型内(白名单)
            try {
                String fileHeader = Files.getFileHeader(file.getInputStream());
                System.out.println("[ups file] --- " + entry.getKey() + " => " + file.getOriginalFilename() + " | " + fileHeader);

                if (StringUtils.isBlank(fileHeader)) {
                    throw new NoteException("Blank string 'fileHeader'.");
                }

                Whitelist whitelist = super.generalDaoHelper.quickQueryOne(Whitelist.class, "fileHeader", StringUtils.substring(fileHeader, 0, 6));
                if (null == whitelist) {
                    throw new NoteException("Block this file. [fileHeader: " + StringUtils.substring(fileHeader, 0, 6) + "]");
                }

                // todo 判断图片文件是否带有js

                // 如果没有抛出异常 则文件后缀名取白名单中的后缀名
                long time = System.currentTimeMillis();
                fileSavedName = saveTo(time, file.getInputStream(), file.getOriginalFilename(), whitelist.getExt(), super.config.getFileDir());
                saved.put(entry.getKey(), fileSavedName);

                // 数据库保存文件相关数据 并发不高时无所谓
                fileSystem = new FileSystem();
                fileSystem.setSavedFilename(fileSavedName);
                fileSystem.setOriginalFilename(file.getOriginalFilename());
                fileSystem.setSize(file.getSize());
                fileSystem.setEntry(time);
                fileSystem.setService(time);
                super.generalDaoHelper.save(fileSystem);

            } catch (Exception e) {
                failed.put(entry.getKey(), e.getMessage());
            }
        }

        FileSavedResult result = new FileSavedResult();
        result.setSaved(saved);
        result.setFailed(failed);
        return new GeneralResult<FileSavedResult>().setValue(result);

    }

    @Override
    public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        if (null == fileName) {
            throw new NoteException("Null of file name.");
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
            int mb32 = 32 * 1024 * 1024;

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
            this.flush(super.config.getFileDir() + fileName, response, start, reqSize, fileSystem);


        } else {
            // 不需要断点续传 如图片

            // 清空response
            response.reset();
            response.setContentType(whitelist.getContentType());
            this.flush(super.config.getFileDir() + fileName, response, null, Math.toIntExact(fileSystem.getSize()), fileSystem);

        }

    }

    /**
     * 保存图片
     * @return 保存后的文件名
     */
    private String saveTo(long time, InputStream stream,String fileName, String ext, String dir) {
        if (stream == null) {
            throw new RuntimeException("No inputSteam in file.");
        }

        String saveName = time + "-" + Encrypt.md5HexString(time + fileName) + "." + ext;

        try {
            FileOutputStream fos = FileUtils.openOutputStream(new File(dir + saveName));
            IOUtils.copy(stream, fos);

            stream.close();
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return saveName;
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
            byte[] buffer = new byte[4096];

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

            fileSystem.setSize(file.length());
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
