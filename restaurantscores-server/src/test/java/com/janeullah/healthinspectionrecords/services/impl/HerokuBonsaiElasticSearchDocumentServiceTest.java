package com.janeullah.healthinspectionrecords.services.impl;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.dtos.heroku.Acknowledgement;
import com.janeullah.healthinspectionrecords.domain.dtos.heroku.HerokuIndexResponse;
import com.janeullah.healthinspectionrecords.domain.dtos.heroku.Restaurants;
import com.janeullah.healthinspectionrecords.exceptions.HerokuClientException;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HerokuBonsaiElasticSearchDocumentServiceTest {
    @InjectMocks
    private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;
    @Mock
    private HerokuBonsaiServices herokuBonsaiServices;
    @Mock
    private RestaurantRepository restaurantRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiUrl", "https://33sdfsfx:abcdef43@a-b.us-east-1.bonsaisearch.net");
        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiUserName", "33sdfsfx");
        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiPassword", "abcdef43");
    }


    @Test
    public void testAddRestaurantDocuments_Success() {

        ResponseEntity<HttpStatus> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocuments(new ArrayList<>());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddRestaurantDocuments_EmptyList() {

        ResponseEntity<HttpStatus> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocuments(new ArrayList<>());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddRestaurantDocuments_Error() throws HerokuClientException {
        HerokuIndexResponse response = HerokuIndexResponse.builder()
                .restaurants(Restaurants.builder().build())
                .build();
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenReturn(response);

        HerokuClientException error = new HerokuClientException(404, "unspecified error", null);
        when(herokuBonsaiServices.addRestaurantToIndex(anyString(), anyMap(), any(FlattenedRestaurant.class)))
                .thenThrow(error);

        ResponseEntity<HttpStatus> result = herokuBonsaiElasticSearchDocumentService.addRestaurantDocuments(Collections.singletonList(TestUtil.getSingleFlattenedRestaurant()));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }


    @Test
    public void testAddRestaurantDocument_Success() throws HerokuClientException {
        when(herokuBonsaiServices.addRestaurantToIndex(anyString(), anyMap(), any(FlattenedRestaurant.class))).thenReturn(Acknowledgement.builder().build());

        ResponseEntity<HttpStatus> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocument(1L, TestUtil.getSingleFlattenedRestaurant());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testIsIndexPresent_Success() throws HerokuClientException {
        HerokuIndexResponse response = HerokuIndexResponse.builder()
                .restaurants(Restaurants.builder().build())
                .build();
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenReturn(response);
        assertTrue(herokuBonsaiElasticSearchDocumentService.isIndexPresent());
    }

    @Test
    public void testIsIndexPresent_IndexNotFound_Error() throws HerokuClientException {
        HerokuClientException error = new HerokuClientException(404, "index_not_found_exception", null);
        ReflectionTestUtils.setField(error, "errorType", "index_not_found_exception");
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenThrow(error);
        assertFalse(herokuBonsaiElasticSearchDocumentService.isIndexPresent());
    }
}
