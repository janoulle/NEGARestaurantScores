package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//https://spring.io/guides/gs/testing-web/
//https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html
//https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4
@RunWith(SpringRunner.class)
@WebMvcTest(HealthCheckController.class)
public class HealthCheckControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FirebaseInitialization firebaseInitialization;

    @Test
    public void testCheckIsAlive() throws Exception {
        mvc.perform(get("/healthcheck/isAlive"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK")));
    }

    @Test
    public void testIsFirebaseInitialized_True() throws Exception {
        when(firebaseInitialization.isDatabaseInitialized()).thenReturn(true);
        mvc.perform(get("/healthcheck/testFirebaseConnectivity"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
