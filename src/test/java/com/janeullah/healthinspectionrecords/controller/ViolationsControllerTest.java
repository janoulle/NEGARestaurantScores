package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.repository.ViolationRepository;
import com.janeullah.healthinspectionrecords.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//https://www.petrikainulainen.net/programming/spring-framework/unit-testing-of-spring-mvc-controllers-rest-api/
@RunWith(SpringRunner.class)
@WebMvcTest(ViolationsController.class)
public class ViolationsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ViolationRepository violationRepository;

    @Test
    public void testGetViolationById() throws Exception {
        Violation violation = TestUtil.getSingleViolation(1L, Severity.CRITICAL, "Meat slicer not clean to sight - PIC had it cleaned.");

        when(violationRepository.findById(1L)).thenReturn(Optional.of(violation));

        mvc.perform(get("/violations/id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.notes", is(violation.getNotes())))
                .andExpect(jsonPath("$.severity", is("CRITICAL")));
    }

    @Test
    public void testFindViolationsByRestaurantId() throws Exception {
        Violation violation = TestUtil.getSingleViolation(1L, Severity.NONCRITICAL, "Violation of 2-A");

        when(violationRepository.findViolationsByRestaurantId(5L)).thenReturn(Collections.singletonList(violation));

        mvc.perform(get("/violations/restaurantId/5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].notes", is("Violation of 2-A")))
                .andExpect(jsonPath("$[0].severity", is("NONCRITICAL")));
    }

    @Test
    public void testgetViolationsByCode() throws Exception {
        Violation violation = TestUtil.getSingleViolation(1L, Severity.NONCRITICAL, "Uncovered salsa in reach in Cooler - PIC covered it.");
        violation.setCategory("4-2A");

        when(violationRepository.findByCategory("4-2A")).thenReturn(Collections.singletonList(violation));

        mvc.perform(get("/violations/category/4-2A"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].notes", is(violation.getNotes())))
                .andExpect(jsonPath("$[0].severity", is("NONCRITICAL")));
    }
}
