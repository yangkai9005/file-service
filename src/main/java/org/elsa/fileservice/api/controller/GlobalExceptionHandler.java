package org.elsa.fileservice.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.elsa.fileservice.api.response.GeneralResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author valor
 * @date 2018/9/25 16:30
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    @ExceptionHandler(value = {Exception.class})
    public GeneralResult<String> handleNoteException(Exception e) {

        StringBuilder builder = new StringBuilder();

        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement element : trace) {
            if (StringUtils.startsWith(element.getClassName(), "org.elsa")) {
                builder.append(element.getFileName())
                        .append("[").append(element.getLineNumber()).append("]")
                        .append("&");
            }
        }

        builder.deleteCharAt(builder.length() - 1);

        GeneralResult<String> result = new GeneralResult<>();
        result.setSuccess(false);
        result.setMessage(e.getMessage());
        result.setValue(super.request.getRequestURL().toString() + " -- > " + builder.toString());

        // let gc do its work
        builder = null;
        trace = null;

        return result;
    }
}
