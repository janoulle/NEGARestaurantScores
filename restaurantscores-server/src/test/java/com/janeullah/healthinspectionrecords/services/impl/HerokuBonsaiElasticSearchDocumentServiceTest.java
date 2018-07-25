package com.janeullah.healthinspectionrecords.services.impl;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.rest.RemoteRestClient;
import com.janeullah.healthinspectionrecords.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HerokuBonsaiElasticSearchDocumentServiceTest {
    @InjectMocks
    private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;
    @Mock
    private RemoteRestClient restClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiUrl", "https://33sdfsfx:abcdef43@a-b.us-east-1.bonsaisearch.net");
        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiUserName", "33sdfsfx");
        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiPassword", "abcdef43");
    }


    @Test
    public void testAddRestaurantDocuments_Success() {

        ResponseEntity<String> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocuments(new ArrayList<>());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddRestaurantDocuments_EmptyList() {

        ResponseEntity<String> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocuments(new ArrayList<>());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddRestaurantDocuments_Error() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        HttpEntity<FlattenedRestaurant> httpEntity = mock(HttpEntity.class);

        when(restClient.getHttpRequestEntityForExchange(any(FlattenedRestaurant.class), anyMap())).thenReturn(httpEntity);
        when(restClient.getHttpsRestTemplate()).thenReturn(mockRestTemplate);
        when(mockRestTemplate.exchange(anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<String> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocuments(Collections.singletonList(TestUtil.getSingleFlattenedRestaurant()));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    public void testAddRestaurantDocument_Success() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        HttpEntity<FlattenedRestaurant> httpEntity = mock(HttpEntity.class);

        when(restClient.getHttpRequestEntityForExchange(any(FlattenedRestaurant.class), anyMap())).thenReturn(httpEntity);
        when(restClient.getHttpsRestTemplate()).thenReturn(mockRestTemplate);
        when(mockRestTemplate.exchange(anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<String> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocument(1L, TestUtil.getSingleFlattenedRestaurant());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
