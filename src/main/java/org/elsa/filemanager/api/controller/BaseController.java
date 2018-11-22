package org.elsa.filemanager.api.controller;

import org.elsa.filemanager.api.response.GeneralResult;
import org.elsa.filemanager.api.response.adapter.FileSavedResult;
import org.elsa.filemanager.common.dao.GeneralDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author valor
 * @date 2018/11/21 13:34
 */
@RestController
public abstract class BaseController {

    @Value("${file-dir}")
    protected String fileDir;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected GeneralDaoHelper generalDaoHelper;

    /**
     * 上传图片
     */
    @RequestMapping(value = "/ups", method = RequestMethod.POST)
    public abstract GeneralResult<FileSavedResult> upload();

    /**
     * 下载图片
     */
    @RequestMapping(value = "/get/{fileName}", method = RequestMethod.POST)
    public abstract GeneralResult<String> getFile(@PathVariable("fileName") String fileName);
}
