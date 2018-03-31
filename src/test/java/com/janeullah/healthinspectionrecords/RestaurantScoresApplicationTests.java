package com.janeullah.healthinspectionrecords;

import com.janeullah.healthinspectionrecords.events.WebEventOrchestrator;
import com.janeullah.healthinspectionrecords.events.WebPageDownloader;
import com.janeullah.healthinspectionrecords.events.WebPageProcessing;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
  WebPageDownloader.class,
})
public class RestaurantScoresApplicationTests {

  @InjectMocks WebEventOrchestrator webEventOrchestrator;

  @Mock WebPageDownloader webPageDownloader;

  @Mock WebPageProcessing webPageProcessing;

  @Before
  public void setup() {
    mockStatic(WebPageDownloader.class);
  }

  @Test
  public void testingNoWebPageRedownloads() {
    when(WebPageDownloader.isDataExpired()).thenReturn(false);
    // when(webPageDownloader.initiateDownloadsAndProcessFiles()).thenReturn(false);
  }
}
