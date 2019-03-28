package org.elsa.fileservice.advice.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author valor
 * @date 2018-11-23 16:07
 */
@Component
@ConfigurationProperties(prefix = "upload")
@Data
public class UploadConf {

    /**
     * 文件保存目录
     */
    private String fileDir;

    /**
     * 临时文件存储目录
     */
    private String tmpDir;

    /**
     * 上传内存缓冲 128M
     */
    private Integer upsBuffer;

    /**
     * 下载内存缓冲 32M
     */
    private Integer getBuffer;

    /**
     * 每次读取的bytes 1M
     */
    private Integer readBytes;

    /**
     * 上传凭证过期时间 ms
     */
    private Integer timeout;

    /**
     * 文件上传最大
     */
    private Long fileMax;

    /**
     * 请求大小
     */
    private Long sizeMax;

    public String getFileDir() {
        if (!StringUtils.endsWith(this.fileDir, File.separator)) {
            this.fileDir = this.fileDir + File.separator;
        }
        return fileDir;
    }

    public String getTmpDir() {
        if (!StringUtils.endsWith(this.tmpDir, File.separator)) {
            this.tmpDir = this.tmpDir + File.separator;
        }
        return tmpDir;
    }
}
