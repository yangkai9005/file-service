package org.elsa.fileservice.core.entity;

import lombok.Data;
import org.elsa.fileservice.common.utils.Times;

/**
 * @author valor
 * @date 2019-03-27 00:57
 */
@Data
public class UploadCert {

    private Integer id;

    /**
     * 上传文件需要的凭证
     */
    private String certificate;

    /**
     * 凭证是否使用 true 使用 | false 未使用
     */
    private Boolean used;

    /**
     * 凭证颁发的时间戳
     */
    private Long time;

    /**
     * 凭证颁发的时间
     */
    private String timeDisplay;

    public void setTime(Long time) {
        this.time = time;
        if (null == time) {
            this.timeDisplay = null;
        } else {
            this.timeDisplay = Times.format(time, Times.DATE_TIME);
        }
    }
}
