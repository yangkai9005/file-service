package org.elsa.fileservice.api.request;

import lombok.Data;

/**
 * @author valor
 * @date 2019-03-27 01:07
 */
@Data
public class CheckMd5 {

    /**
     * 前端标识
     */
    private String flag;

    /**
     * 文件md5
     */
    private String md5;

}
