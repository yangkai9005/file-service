package org.elsa.filemanager.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.elsa.filemanager.api.response.GeneralResult;
import org.elsa.filemanager.api.response.adapter.FileSavedResult;
import org.elsa.filemanager.common.exception.NoteException;
import org.elsa.filemanager.common.utils.Encrypt;
import org.elsa.filemanager.common.utils.Files;
import org.elsa.filemanager.common.utils.Ips;
import org.elsa.filemanager.core.entity.FileSystem;
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
@Slf4j
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
                log.info("[ups file] --- " + entry.getKey() + " => " + file.getOriginalFilename() + " | " + fileHeader);

                if (StringUtils.isBlank(fileHeader)) {
                    throw new NoteException("Blank string 'fileHeader'.");
                }
                String ext = super.fileTypeManager.getCacheType().get(StringUtils.substring(fileHeader, 0, 8));
                if (StringUtils.isBlank(ext)) {
                    throw new NoteException("Block this file.");
                }

                // todo 判断图片文件是否带有js

                // 如果没有抛出异常 则文件后缀名取白名单中的后缀名
                long time = System.currentTimeMillis();
                fileSavedName = saveTo(time, file.getInputStream(), file.getOriginalFilename(), ext, super.getFileDir());
                saved.put(entry.getKey(), fileSavedName);

                // 数据库保存文件相关数据 并发不高时无所谓
                fileSystem = new FileSystem();
                fileSystem.setSavedFilename(fileSavedName);
                fileSystem.setOriginalFilename(file.getOriginalFilename());
                fileSystem.setCallIp(Ips.getIpAddress(super.request));
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

        FileSystem fileSystem = super.fileMapper.queryByFilename(fileName);
        if (null == fileSystem) {
            throw new NoteException("No such file.");
        }

        InputStream fis = null;
        try {
            // 读取图片流
            fis = new FileInputStream(super.getFileDir() + fileName);
            byte[] bytes = new byte[fis.available()];
            int i = fis.read(bytes);

            if (-1 == i) {
                throw new NoteException("Error reading file to bytes.");
            }

            // 清空response
            response.reset();
            // 设置请求response
            response.setContentType("image/gif");

            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();

            fileSystem.setService(System.currentTimeMillis());
            super.generalDaoHelper.save(fileSystem);

        } catch (Exception e) {
            throw new NoteException(e);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

        String saveName = time + "-" + Encrypt.md5AndBase64(time + fileName) + "." + ext;

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

}
