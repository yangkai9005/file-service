package org.elsa.filemanager.api;

import org.elsa.filemanager.api.response.GeneralResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author valor
 * @date 2018/9/25 16:30
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    HttpServletRequest request;

    @ExceptionHandler(value = {Exception.class})
    public GeneralResult<String> handleNoteException(Exception e) {

        GeneralResult<String> result = new GeneralResult<>();
        result.setSuccess(false);
        result.setMessage(e.getMessage());
        result.setValue(request.getRequestURL().toString());
        return result;
    }
}
