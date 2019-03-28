package org.elsa.fileservice.api.controller;

import org.elsa.fileservice.api.response.GeneralResult;
import org.elsa.fileservice.api.response.ListResult;
import org.elsa.fileservice.api.response.adapter.CheckMd5Result;
import org.elsa.fileservice.api.response.adapter.FileSavedResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author valor
 * @date 2018/11/21 14:34
 */
@RestController
public abstract class FileOpsController extends BaseController {

    /**
     * 上传前的查询
     * 校验md5
     */
    @RequestMapping(value = "/md5", method = RequestMethod.POST)
    public abstract ListResult<CheckMd5Result> checkMd5();

    /**
     * 根据颁发的凭证上传文件
     */
    @RequestMapping(value = "/ups", method = RequestMethod.POST)
    public abstract GeneralResult<FileSavedResult> upload() throws Exception;

    /**
     * 下载图片
     */
    @RequestMapping(value = "/get/{fileName}", method = RequestMethod.GET)
    public abstract void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response);

}
