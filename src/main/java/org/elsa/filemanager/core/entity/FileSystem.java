package org.elsa.filemanager.core.entity;

import lombok.Data;
import org.elsa.filemanager.common.utils.Times;

import java.util.Date;

/**
 * @author valor
 * @date 2018-11-22 15:53
 */
@Data
public class FileSystem {

    private Long id;

    /**
     * 保存的文件名
     */
    private String savedFilename;

    /**
     * 上传的源文件名
     */
    private String originalFilename;

    /**
     * 客户端ip
     */
    private String callIp;

    /**
     * 最后一次使用文件的时间戳
     */
    private Long service;

    /**
     * 最后一次使用文件的时间
     */
    private String serviceDisplay;

    /**
     * 上传文件的时间戳
     */
    private Long entry;

    /**
     * 上传文件的时间
     */
    private String entryDisplay;

    /**
     * 设置注册时间
     */
    public void setEntry(Long entry) {
        this.entry = entry;
        if (null == entry) {
            this.entryDisplay = null;
        } else {
            this.entryDisplay = Times.dateFormat(new Date(entry), Times.DATE_TIME);
        }
    }

    /**
     * 设置注册时间
     */
    public void setService(Long service) {
        this.service = service;
        if (null == service) {
            this.serviceDisplay = null;
        } else {
            this.serviceDisplay = Times.dateFormat(new Date(service), Times.DATE_TIME);
        }
    }
}
