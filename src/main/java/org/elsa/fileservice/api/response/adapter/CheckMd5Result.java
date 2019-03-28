package org.elsa.fileservice.api.response.adapter;

import lombok.Data;
import org.elsa.fileservice.core.entity.FileSystem;
import org.elsa.fileservice.core.entity.UploadCert;

/**
 * @author valor
 * @date 2019-03-27 01:08
 */
@Data
public class CheckMd5Result {

    /**
     * 前端标识
     */
    private String flag;

    /**
     * 校验结果(文件是否已经上传)
     */
    private Boolean uploaded;

    /**
     * 已上传 则显示文件信息
     */
    private FileSystem info;

    /**
     * 未上传 则提供上传凭证
     */
    private UploadCert cert;

}
