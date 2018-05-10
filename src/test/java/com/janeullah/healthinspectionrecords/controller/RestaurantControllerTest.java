package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RestaurantController.class)
public class RestaurantControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private RestaurantRepository restaurantRepository;

  @Test
  public void testGetAllFlattenedRestaurants() throws Exception {
    FlattenedRestaurant restaurant = TestUtil.getSingleFlattenedRestaurant();

    when(restaurantRepository.findAllFlattenedRestaurants())
        .thenReturn(Collections.singletonList(restaurant));

    mvc.perform(get("/restaurants/allFlattened"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].county", is("WALTON")))
        .andExpect(jsonPath("$[0].name", is("ZAXBY'S-MONROE")))
        .andExpect(jsonPath("$[0].address", is("195 MLK JR. BLVD. MONROE GA, 30655")))
        .andExpect(jsonPath("$[0].criticalViolations", is(2)))
        .andExpect(jsonPath("$[0].nonCriticalViolations", is(5)))
        .andExpect(jsonPath("$[0].score", is(84)))
        .andExpect(jsonPath("$[0].dateReported", is("2018-01-29")));
  }


  @Test
  public void testGetAllRestaurants() throws Exception {
    Restaurant restaurant = TestUtil.getSingleRestaurant();

    when(restaurantRepository.findAll()).thenReturn(Collections.singletonList(restaurant));

    mvc.perform(get("/restaurants/all"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].inspectionReports", hasSize(1)))
            .andExpect(jsonPath("$[0].inspectionReports[0].violations", hasSize(1)))
            .andExpect(jsonPath("$[0].inspectionReports[0].dateReported", is("2018-05-05")))
            .andExpect(jsonPath("$[0].inspectionReports[0].inspectionType", is("ROUTINE")))
            .andExpect(jsonPath("$[0].establishmentInfo.name", is("ZAXBY'S-MONROE")))
            .andExpect(jsonPath("$[0].establishmentInfo.address", is("195 MLK JR. BLVD. MONROE GA, 30655")))
            .andExpect(jsonPath("$[0].establishmentInfo.county", is("Walton")))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].establishmentInfo.id", is(4)));
  }
}
