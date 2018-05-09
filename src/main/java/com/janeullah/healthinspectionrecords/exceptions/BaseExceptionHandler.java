package com.janeullah.healthinspectionrecords.exceptions;

import com.janeullah.healthinspectionrecords.exceptions.notifications.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * https://github.com/nielsutrecht/controller-advice-exception-handler/blob/master/examples/src/main/java/com/nibado/example/errorhandlers/example4/BaseExceptionHandler.java
 * Date: 4/28/2017
 */
@ControllerAdvice
public class BaseExceptionHandler {
  private static final ExceptionMapping DEFAULT_ERROR =
      new ExceptionMapping(
          "INTERNAL_SERVER_ERROR",
          "Unhandled exception encountered",
          HttpStatus.INTERNAL_SERVER_ERROR);
  private static Logger logger = LoggerFactory.getLogger(BaseExceptionHandler.class);

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  @ResponseBody
  public ErrorResponse handleThrowable(final Throwable ex) {
    logger.error("Unhandled exception encountered", ex);
    return new ErrorResponse(DEFAULT_ERROR.code, DEFAULT_ERROR.message, ex);
  }

  /**
   * Demonstrates how to take total control - setup a model, add useful information and return the
   * "support" view name. This method explicitly creates and returns
   *
   * @param req Current HTTP request.
   * @param exception The exception thrown - always {@link IncompatibleConfigurationException}.
   * @return The model and view used by the DispatcherServlet to generate output.
   * @throws Exception
   */
  @ExceptionHandler(IncompatibleConfigurationException.class)
  public ModelAndView handleError(HttpServletRequest req, Exception exception) throws Exception {

    // Rethrow annotated exceptions or they will be processed here instead.
    if (AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class) != null)
      throw exception;

    logger.error("Request: {} raised ", req.getRequestURI(), exception);

    ModelAndView mav = new ModelAndView();
    mav.addObject("exception", exception);
    mav.addObject("url", req.getRequestURL());
    mav.addObject("timestamp", new Date().toString());
    mav.addObject("status", 500);

    mav.setViewName("support");
    return mav;
  }

  private static class ExceptionMapping {
    private String message;
    private String code;
    private HttpStatus status;

    public ExceptionMapping(String code, String message, HttpStatus status) {
      this.message = message;
      this.code = code;
      this.status = status;
    }
  }
}
