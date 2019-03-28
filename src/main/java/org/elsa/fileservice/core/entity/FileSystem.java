package org.elsa.fileservice.core.entity;

import lombok.Data;
import org.elsa.fileservice.common.utils.Times;

/**
 * @author valor
 * @date 2019-03-27 00:36
 */
@Data
public class FileSystem {

    private Integer id;

    /**
     * 文件md5
     */
    private String fileMd5;

    /**
     * 保存的文件名
     */
    private String savedFilename;

    /**
     * 源文件名
     */
    private String originalFilename;

    /**
     * 上传文件的时间戳
     */
    private Long entry;

    /**
     * 上传文件的时间
     */
    private String entryDisplay;

    /**
     * 文件大小 单位byte(b)
     */
    private Long size;

    /**
     * 下载次数
     */
    private Integer number;

    /**
     * 最后一次使用文件的时间戳
     */
    private Long service;

    /**
     * 最后一次使用文件的时间
     */
    private String serviceDisplay;

    public void setEntry(Long entry) {
        this.entry = entry;
        if (null == entry) {
            this.entryDisplay = null;
        } else {
            this.entryDisplay = Times.format(entry, Times.DATE_TIME);
        }
    }

    public void setService(Long service) {
        this.service = service;
        if (null == service) {
            this.serviceDisplay = null;
        } else {
            this.serviceDisplay = Times.format(service, Times.DATE_TIME);
        }
    }

}
