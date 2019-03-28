package org.elsa.fileservice.advice;

import org.elsa.fileservice.advice.config.UploadConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author valor
 * @date 2018-12-12 19:02
 */
@Component
public abstract class BaseConfiguration {

    @Autowired
    protected UploadConf uploadConf;

}
