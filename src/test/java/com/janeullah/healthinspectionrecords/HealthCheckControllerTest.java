package com.janeullah.healthinspectionrecords;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class HealthCheckControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testCheckIsAlive() throws Exception {
//        mvc.perform(MockMvcRequestBuilders.get("/isAlive").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string(equalTo("OK")));
    }
}