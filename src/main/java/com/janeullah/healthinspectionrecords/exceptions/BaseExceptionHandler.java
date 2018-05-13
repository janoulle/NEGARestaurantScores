package com.janeullah.healthinspectionrecords.exceptions;

import com.janeullah.healthinspectionrecords.exceptions.notifications.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * https://github.com/nielsutrecht/controller-advice-exception-handler/blob/master/examples/src/main/java/com/nibado/example/errorhandlers/example4/BaseExceptionHandler.java
 * Date: 4/28/2017
 */
@ControllerAdvice
public class BaseExceptionHandler {
  private static Logger logger = LoggerFactory.getLogger(BaseExceptionHandler.class);

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  @ResponseBody
  public ErrorResponse handleThrowable(final Throwable ex) {
    logger.error("Unhandled exception encountered", ex);
    return new ErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), ex);
  }

}
