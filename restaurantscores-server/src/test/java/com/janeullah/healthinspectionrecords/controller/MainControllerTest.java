package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.events.WebEventOrchestrator;
import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.services.impl.AwsElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.impl.HerokuBonsaiElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.impl.LocalhostElasticSearchDocumentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WebEventOrchestrator webEventOrchestrator;
    @MockBean
    private FirebaseInitialization firebaseInitialization;
    @MockBean
    private LocalhostElasticSearchDocumentService localhostElasticSearchDocumentService;
    @MockBean
    private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;
    @MockBean
    private AwsElasticSearchDocumentService awsElasticSearchDocumentService;
    @MockBean
    private RestaurantRepository restaurantRepository;

    @Test
    public void testInitializeLocalDB() throws Exception {
        mvc.perform(put("/admin/initializeLocalDB"))
                .andExpect(status().isOk());
    }

    @Test
    public void testInitializeFirebaseDB_Success() throws Exception {
        when(firebaseInitialization.readRecordsFromLocalAndWriteToRemote()).thenReturn(true);
        mvc.perform(put("/admin/initializeFirebaseDB"))
                .andExpect(status().isOk());
    }

    @Test
    public void testInitializeFirebaseDB_Failure() throws Exception {
        when(firebaseInitialization.readRecordsFromLocalAndWriteToRemote()).thenReturn(false);
        mvc.perform(put("/admin/initializeFirebaseDB"))
                .andExpect(status().isFailedDependency());
    }

    @Test
    public void testSeedElasticSearchDBLocal() throws  Exception {
        when(restaurantRepository.findAllFlattenedRestaurants()).thenReturn(new ArrayList<>());
        when(localhostElasticSearchDocumentService.addRestaurantDocuments(anyList())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/admin/seedElasticSearchDBLocal"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSeedElasticSearchDBAWS() throws  Exception {
        when(restaurantRepository.findAllFlattenedRestaurants()).thenReturn(new ArrayList<>());
        when(awsElasticSearchDocumentService.addRestaurantDocuments(anyList())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/admin/seedElasticSearchDBAWS"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSeedElasticSearchDBHeroku() throws  Exception {
        when(restaurantRepository.findAllFlattenedRestaurants()).thenReturn(new ArrayList<>());
        when(herokuBonsaiElasticSearchDocumentService.addRestaurantDocuments(anyList())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/admin/seedElasticSearchDBHeroku"))
                .andExpect(status().isOk());
    }
}
