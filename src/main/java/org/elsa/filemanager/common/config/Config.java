package org.elsa.filemanager.common.config;

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
@ConfigurationProperties(prefix = "config")
@Data
public class Config {

    /**
     * 存储目录
     */
    private String fileDir;

    /**
     * 过期时间
     */
    private Long expiredDay;

    public String getFileDir() {
        String dir = this.fileDir;
        if (!StringUtils.endsWith(this.fileDir, File.separator)) {
            dir = this.fileDir + File.separator;
        }
        return dir;
    }
}
