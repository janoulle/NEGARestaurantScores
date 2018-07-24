package com.janeullah.healthinspectionrecords.events;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebEventOrchestratorTest {

    @InjectMocks
    private WebEventOrchestrator webEventOrchestrator;

    @Mock
    private WebPageDownloader webPageDownloader;

    @Mock
    private WebPageProcessing webPageProcessing;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsDownloadOverrideOrDataExpired_False() {
        when(webPageDownloader.isDownloadOverrideOrDataExpired()).thenReturn(false);
        webEventOrchestrator.processAndSaveAllRestaurants();

        verify(webPageDownloader, never()).initiateDownloadsAndProcessFiles();
        verify(webPageProcessing, times(1)).startProcessingOfDownloadedFiles();
    }


    @Test
    public void testIsDownloadOverrideOrDataExpired_True() {
        when(webPageDownloader.isDownloadOverrideOrDataExpired()).thenReturn(true);
        webEventOrchestrator.processAndSaveAllRestaurants();

        verify(webPageProcessing, never()).startProcessingOfDownloadedFiles();
        verify(webPageDownloader, times(1)).initiateDownloadsAndProcessFiles();
    }

    @Test
    public void testExceptionalScenario() {
        when(webPageDownloader.isDownloadOverrideOrDataExpired()).thenThrow(new NullPointerException("ahh!"));
        webEventOrchestrator.processAndSaveAllRestaurants();

        verify(webPageDownloader, never()).initiateDownloadsAndProcessFiles();
        verify(webPageProcessing, never()).startProcessingOfDownloadedFiles();

    }

}
