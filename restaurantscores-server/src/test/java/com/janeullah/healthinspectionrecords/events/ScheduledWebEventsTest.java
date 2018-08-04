package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.services.impl.HerokuBonsaiElasticSearchDocumentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledWebEventsTest {
    @InjectMocks
    private ScheduledWebEvents scheduledWebEvents;
    @Mock
    private FirebaseInitialization firebaseInitialization;
    @Mock
    private WebEventOrchestrator webEventOrchestrator;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;

    @Test
    public void testRunAllUpdates_Success() {
        when(restaurantRepository.count()).thenReturn(10L);
        when(firebaseInitialization.readRecordsFromLocalAndWriteToRemote()).thenReturn(true);
        when(herokuBonsaiElasticSearchDocumentService.handleProcessingOfData()).thenReturn(true);

        assertTrue(scheduledWebEvents.runAllUpdates());
        verify(restaurantRepository, times(1)).deleteAll();
        verify(webEventOrchestrator, times(1)).processAndSaveAllRestaurants();
        verify(firebaseInitialization, times(1)).readRecordsFromLocalAndWriteToRemote();
        verify(herokuBonsaiElasticSearchDocumentService, times(1)).handleProcessingOfData();
    }

    @Test
    public void testRunAllUpdates_NoRestaurants() {
        when(restaurantRepository.count()).thenReturn(0L);

        assertFalse(scheduledWebEvents.runAllUpdates());
        verify(restaurantRepository, times(1)).deleteAll();
        verify(webEventOrchestrator, times(1)).processAndSaveAllRestaurants();
        verify(firebaseInitialization, times(0)).readRecordsFromLocalAndWriteToRemote();
        verify(herokuBonsaiElasticSearchDocumentService, times(0)).handleProcessingOfData();
    }

    @Test
    public void testRunAllUpdates_NoFirebaseSave() {
        when(restaurantRepository.count()).thenReturn(20L);
        when(firebaseInitialization.readRecordsFromLocalAndWriteToRemote()).thenReturn(false);

        assertFalse(scheduledWebEvents.runAllUpdates());
        verify(restaurantRepository, times(1)).deleteAll();
        verify(webEventOrchestrator, times(1)).processAndSaveAllRestaurants();
        verify(firebaseInitialization, times(1)).readRecordsFromLocalAndWriteToRemote();
        verify(herokuBonsaiElasticSearchDocumentService, times(0)).handleProcessingOfData();
    }

    @Test
    public void testRunAllUpdates_NoHerokuSave() {
        when(restaurantRepository.count()).thenReturn(20L);
        when(firebaseInitialization.readRecordsFromLocalAndWriteToRemote()).thenReturn(true);
        when(herokuBonsaiElasticSearchDocumentService.handleProcessingOfData()).thenReturn(false);

        assertFalse(scheduledWebEvents.runAllUpdates());
        verify(restaurantRepository, times(1)).deleteAll();
        verify(webEventOrchestrator, times(1)).processAndSaveAllRestaurants();
        verify(firebaseInitialization, times(1)).readRecordsFromLocalAndWriteToRemote();
        verify(herokuBonsaiElasticSearchDocumentService, times(1)).handleProcessingOfData();
    }
}
