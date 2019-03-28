package org.elsa.fileservice.api.controller;

import org.elsa.fileservice.advice.cache.UploadCache;
import org.elsa.fileservice.advice.config.UploadConf;
import org.elsa.fileservice.common.dao.GeneralDaoHelper;
import org.elsa.fileservice.core.mapper.CertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author valor
 * @date 2018/11/21 13:34
 */
@RestController
public abstract class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected GeneralDaoHelper generalDaoHelper;

    @Autowired
    protected UploadConf uploadConf;

    @Autowired
    protected UploadCache uploadCache = UploadCache.getInstance();

    @Autowired
    protected CertMapper certMapper;

}
