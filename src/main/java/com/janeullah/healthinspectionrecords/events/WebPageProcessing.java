package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.async.WebPageProcessService;
import com.janeullah.healthinspectionrecords.constants.PathVariables;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/** Author: Jane Ullah Date: 9/17/2016 */
@Slf4j
@Component
public class WebPageProcessing {
  private static final CountDownLatch COUNT_DOWN_LATCH =
      new CountDownLatch(WebPageConstants.COUNTY_LIST.size());
  private PathVariables pathVariables;
  private WebPageProcessService webPageProcessService;

  @Autowired
  public WebPageProcessing(
      WebPageProcessService webPageProcessService, PathVariables pathVariables) {
    this.webPageProcessService = webPageProcessService;
    this.pathVariables = pathVariables;
  }

  public void startProcessingOfDownloadedFiles() {
    try {
      File[] files = pathVariables.getFilesInDefaultDirectory();
      for (File file : files) {
        webPageProcessService.submitFileForProcessing(file.toPath(), COUNT_DOWN_LATCH);
      }
      // wait for processing to complete
      COUNT_DOWN_LATCH.await();
    } catch (Exception e) {
      log.error("Exception in startProcessingOfDownloadedFiles", e);
    }
  }
}
