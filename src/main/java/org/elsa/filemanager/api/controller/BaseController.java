package org.elsa.filemanager.api.controller;

import org.elsa.filemanager.api.response.GeneralResult;
import org.elsa.filemanager.api.response.adapter.FileSavedResult;
import org.elsa.filemanager.common.cache.FileType;
import org.elsa.filemanager.common.config.Config;
import org.elsa.filemanager.common.dao.GeneralDaoHelper;
import org.elsa.filemanager.core.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author valor
 * @date 2018/11/21 13:34
 */
@RestController
public abstract class BaseController {

    protected static final String NCR = "token-ncr";

    @Autowired
    protected Config config;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected GeneralDaoHelper generalDaoHelper;

    @Autowired
    protected FileMapper fileMapper;

    @Autowired
    protected FileType fileTypeManager = FileType.getInstance();

    /**
     * 刷新缓存
     */
    @RequestMapping(value = "/flush", method = RequestMethod.GET)
    public abstract GeneralResult<String> flush();

    /**
     * 上传图片
     */
    @RequestMapping(value = "/ups", method = RequestMethod.POST)
    public abstract GeneralResult<FileSavedResult> upload();

    /**
     * 下载图片
     */
    @RequestMapping(value = "/get/{fileName}", method = RequestMethod.GET)
    public abstract void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response);
}
