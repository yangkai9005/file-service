package org.elsa.filemanager.core.entity;

import lombok.Data;

/**
 * @author valor
 * @date 2019-02-14 14:38
 */
@Data
public class Whitelist {

    /**
     * 文件后缀名
     */
    private String ext;

    /**
     * 文件头
     */
    private String fileHeader;

    /**
     * 文件content-type
     */
    private String contentType;

    /**
     * 是否需要断点续传
     */
    private Boolean range;

}
