package org.elsa.filemanager.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.elsa.filemanager.api.response.GeneralResult;
import org.elsa.filemanager.api.response.adapter.FileSavedResult;
import org.elsa.filemanager.common.cache.FileType;
import org.elsa.filemanager.common.dao.GeneralDaoHelper;
import org.elsa.filemanager.core.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @author valor
 * @date 2018/11/21 13:34
 */
@RestController
public abstract class BaseController {

    @Value("${config.file-dir}")
    private String fileDir;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected GeneralDaoHelper generalDaoHelper;

    @Autowired
    protected FileMapper fileMapper;

    @Autowired
    protected FileType fileTypeManager = FileType.getInstance();

    protected String getFileDir() {
        String dir = this.fileDir;
        if (!StringUtils.endsWith(this.fileDir, File.separator)) {
            dir = this.fileDir + File.separator;
        }
        return dir;
    }

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
