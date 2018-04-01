package com.janeullah.healthinspectionrecords;

import com.janeullah.healthinspectionrecords.events.WebEventOrchestrator;
import com.janeullah.healthinspectionrecords.events.WebPageDownloader;
import com.janeullah.healthinspectionrecords.events.WebPageProcessing;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantScoresApplicationTests {

  @InjectMocks WebEventOrchestrator webEventOrchestrator;

  @Mock WebPageDownloader webPageDownloader;

  @Mock WebPageProcessing webPageProcessing;

  @Test
  public void testingNoWebPageRedownloads() {
    when(webPageDownloader.isDownloadOverrideOrDataExpired()).thenReturn(false);
  }
}
