package com.janeullah.healthinspectionrecords.services.impl;

import com.google.gson.Gson;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.dtos.heroku.Acknowledgement;
import com.janeullah.healthinspectionrecords.domain.dtos.heroku.HerokuIndexResponse;
import com.janeullah.healthinspectionrecords.exceptions.HerokuClientException;
import com.janeullah.healthinspectionrecords.services.internal.RestaurantService;
import com.janeullah.healthinspectionrecords.util.TestFileUtil;
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HerokuBonsaiElasticSearchDocumentServiceTest {
    @InjectMocks
    private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;
    @Mock
    private HerokuBonsaiServices herokuBonsaiServices;
    @Mock
    private RestaurantService restaurantService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

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
    public void testAddRestaurantDocuments_Error() throws HerokuClientException, IOException {
        HerokuIndexResponse response = getHerokuIndexResponse();
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenReturn(response);

        HerokuClientException error = new HerokuClientException(404, "unspecified error", null);
        when(herokuBonsaiServices.addRestaurantToIndex(anyString(), anyMap(), any(FlattenedRestaurant.class)))
                .thenThrow(error);

        ResponseEntity<HttpStatus> result = herokuBonsaiElasticSearchDocumentService.addRestaurantDocuments(Collections.singletonList(TestUtil.getSingleFlattenedRestaurant()));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }


    @Test
    public void testAddRestaurantDocument_UnexpectedException() throws HerokuClientException {
        when(herokuBonsaiServices.addRestaurantToIndex(anyString(), anyMap(), any(FlattenedRestaurant.class))).thenThrow(new IllegalArgumentException("blah"));

        ResponseEntity<HttpStatus> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocument(1L, TestUtil.getSingleFlattenedRestaurant());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testAddRestaurantDocument_Success() throws HerokuClientException {
        when(herokuBonsaiServices.addRestaurantToIndex(anyString(), anyMap(), any(FlattenedRestaurant.class))).thenReturn(Acknowledgement.builder().build());

        ResponseEntity<HttpStatus> response = herokuBonsaiElasticSearchDocumentService.addRestaurantDocument(1L, TestUtil.getSingleFlattenedRestaurant());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testHandleProcessingOfData_Success() throws HerokuClientException, IOException {
        HerokuIndexResponse response = getHerokuIndexResponse();
        when(restaurantService.findAllFlattenedRestaurants()).thenReturn(new ArrayList<>());
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenReturn(response);

        assertTrue(herokuBonsaiElasticSearchDocumentService.handleProcessingOfData());

    }

    @Test
    public void testGetAuthHeaders_Success() {

        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiUserName", "smart");
        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiPassword", "lady");
        Map<String, String> expectedResult = herokuBonsaiElasticSearchDocumentService.getAuthHeaders();
        assertEquals("Basic c21hcnQ6bGFkeQ==", expectedResult.get("Authorization"));
    }

    @Test
    public void testGetAuthHeaders_Error() {

        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiUserName", "smart");
        ReflectionTestUtils.setField(herokuBonsaiElasticSearchDocumentService, "herokuBonsaiPassword", "lady");
        Map<String, String> expectedResult = herokuBonsaiElasticSearchDocumentService.getAuthHeaders();
        assertEquals("Basic c21hcnQ6bGFkeQ==", expectedResult.get("Authorization"));
    }

    @Test
    public void testIsIndexSetupOkay_Success() throws HerokuClientException, IOException {
        HerokuIndexResponse response = getHerokuIndexResponse();
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenReturn(response);
        assertTrue(herokuBonsaiElasticSearchDocumentService.isIndexSetupOkay());
        verify(herokuBonsaiServices, times(1)).deleteRestaurantsIndex(anyMap());
        verify(herokuBonsaiServices, times(1)).createRestaurantsIndex(anyMap());
    }

    @Test
    public void testIsIndexSetupOkay_IndexNonExistent_Success() throws HerokuClientException {
        HerokuClientException error = new HerokuClientException(404, "index_not_found_exception", null);
        ReflectionTestUtils.setField(error, "errorType", "index_not_found_exception");
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenThrow(error);
        assertTrue(herokuBonsaiElasticSearchDocumentService.isIndexSetupOkay());

        verify(herokuBonsaiServices, times(0)).deleteRestaurantsIndex(anyMap());
        verify(herokuBonsaiServices, times(1)).createRestaurantsIndex(anyMap());
    }


    @Test
    public void testIsIndexSetupOkay_IndexNotFound_ErrorCreatingIndex() throws HerokuClientException {
        HerokuClientException error = new HerokuClientException(404, "index_not_found_exception", null);
        ReflectionTestUtils.setField(error, "errorType", "index_not_found_exception");
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenThrow(error);
        when(herokuBonsaiServices.createRestaurantsIndex(anyMap())).thenThrow(new NullPointerException("blah"));
        assertFalse(herokuBonsaiElasticSearchDocumentService.isIndexSetupOkay());
        verify(herokuBonsaiServices, times(0)).deleteRestaurantsIndex(anyMap());
        verify(herokuBonsaiServices, times(1)).createRestaurantsIndex(anyMap());
    }

    @Test
    public void testIsIndexPresent_Success() throws HerokuClientException, IOException {
        HerokuIndexResponse response = getHerokuIndexResponse();
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenReturn(response);
        assertTrue(herokuBonsaiElasticSearchDocumentService.isIndexPresent());
    }

    @Test
    public void testIsIndexPresent_IndexNotFound_Error() throws HerokuClientException {

        HerokuClientException error = new HerokuClientException(404, "index_not_found_exception", TestFileUtil.INDEX_NOT_EXISTING);
        when(herokuBonsaiServices.getRestaurantIndex(anyMap())).thenThrow(error);
        assertFalse(herokuBonsaiElasticSearchDocumentService.isIndexPresent());
    }

    private HerokuIndexResponse getHerokuIndexResponse() throws IOException {
        String mappingResponse = TestFileUtil.readFile("src/test/resources/heroku/restaurants-mapping.json", Charset.forName("UTF-8"));
        return new Gson().fromJson(mappingResponse, HerokuIndexResponse.class);
    }
}
