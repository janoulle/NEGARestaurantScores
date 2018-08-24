package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.events.ScheduledWebEvents;
import com.janeullah.healthinspectionrecords.events.WebEventOrchestrator;
import com.janeullah.healthinspectionrecords.services.external.firebase.FirebaseInitialization;
import com.janeullah.healthinspectionrecords.services.external.heroku.HerokuBonsaiElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.internal.RestaurantService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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
    private CacheManager cacheManager;
    @MockBean
    private WebEventOrchestrator webEventOrchestrator;
    @MockBean
    private FirebaseInitialization firebaseInitialization;
    @MockBean
    private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;
    @MockBean
    private RestaurantService restaurantService;
    @MockBean
    private ScheduledWebEvents scheduledWebEvents;

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
    public void testSeedElasticSearchDBHeroku_Success() throws Exception {
        when(herokuBonsaiElasticSearchDocumentService.handleProcessingOfData()).thenReturn(true);

        mvc.perform(post("/admin/seedElasticSearchDBHeroku"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSeedElasticSearchDBHeroku_Failure() throws Exception {
        when(herokuBonsaiElasticSearchDocumentService.handleProcessingOfData()).thenReturn(false);

        mvc.perform(post("/admin/seedElasticSearchDBHeroku"))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testRunAll_Success() throws Exception {
        when(scheduledWebEvents.runAllUpdates()).thenReturn(true);

        mvc.perform(put("/admin/runAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRunAll_Failure() throws Exception {
        when(scheduledWebEvents.runAllUpdates()).thenReturn(false);

        mvc.perform(put("/admin/runAll"))
                .andExpect(status().isBadRequest());
    }
}
