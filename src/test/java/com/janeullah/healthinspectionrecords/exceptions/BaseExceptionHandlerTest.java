package com.janeullah.healthinspectionrecords.exceptions;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

// https://github.com/nielsutrecht/controller-advice-exception-handler/blob/master/my-service/src/test/java/com/nibado/example/errorhandlers/service/controller/UserControllerTest.java
// https://github.com/spring-projects/spring-framework/blob/2dd587596437a4bbe9f62ba0dc9f7b13382fb533/spring-test/src/test/java/org/springframework/test/web/servlet/samples/standalone/ExceptionHandlerTests.java
public class BaseExceptionHandlerTest {

  private MockMvc mockMvc;

  @Before
  public void setup() {
    mockMvc =
        standaloneSetup(new FakeController())
            .setControllerAdvice(new BaseExceptionHandler())
            .build();
  }

  @Test
  public void testGlobalExceptionHandlerMethod() throws Exception {

    mockMvc
        .perform(get("/fakeController/testError"))
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.code", is("INTERNAL_SERVER_ERROR")))
        .andExpect(jsonPath("$.message", is("simulated exception")))
        .andExpect(jsonPath("$.throwable.stackTrace[0].fileName", is("BaseExceptionHandlerTest.java")))
        .andExpect(
            jsonPath(
                "$.throwable.stackTrace[0].className",
                is(
                    "com.janeullah.healthinspectionrecords.exceptions.BaseExceptionHandlerTest$FakeController")));
  }

  @Controller
  private static class FakeController {

    @RequestMapping(value = "/fakeController/testError", method = RequestMethod.GET)
    public String simulateException() {
      throw new IllegalStateException("simulated exception");
    }
  }
}
