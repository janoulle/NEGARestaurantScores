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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalhostElasticSearchDocumentServiceTest {
    @InjectMocks
    private LocalhostElasticSearchDocumentService localhostElasticSearchDocumentService;
    @Mock
    private RemoteRestClient restClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(
                localhostElasticSearchDocumentService,
                "localhostUrl",
                "https://localhost:9200");
        when(restClient
                .getRestTemplate()).thenReturn(mock(RestTemplate.class));
    }

    @Test
    public void testAddRestaurant() {
        when(restClient
                .getRestTemplate()
                .postForEntity(anyString(), any(FlattenedRestaurant.class), eq(String.class), anyMap())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<String> response = localhostElasticSearchDocumentService.addRestaurantDocument(1L, TestUtil.getSingleFlattenedRestaurant());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}