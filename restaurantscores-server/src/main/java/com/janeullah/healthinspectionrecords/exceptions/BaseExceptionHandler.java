package com.janeullah.healthinspectionrecords.exceptions;

import com.janeullah.healthinspectionrecords.exceptions.notifications.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * https://github.com/nielsutrecht/controller-advice-exception-handler/blob/master/examples/src/main/java/com/nibado/example/errorhandlers/example4/BaseExceptionHandler.java
 * http://niels.nu/blog/2016/controller-advice-exception-handlers.html
 * https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#global-exception-handling
 * Date: 4/28/2017
 */
@Slf4j
@ControllerAdvice
public class BaseExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ErrorResponse handleThrowable(final Throwable ex) {
        log.error("Unhandled exception encountered", ex);
        return new ErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), ex);
    }

}
