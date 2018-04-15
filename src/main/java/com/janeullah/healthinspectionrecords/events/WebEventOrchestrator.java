package com.janeullah.healthinspectionrecords.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Author: Jane Ullah Date: 9/18/2016 */
@Slf4j
@Component
public class WebEventOrchestrator {
  private WebPageDownloader webPageDownloader;
  private WebPageProcessing webPageProcessing;

  @Autowired
  public WebEventOrchestrator(
      WebPageDownloader webPageDownloader, WebPageProcessing webPageProcessing) {
    this.webPageDownloader = webPageDownloader;
    this.webPageProcessing = webPageProcessing;
  }

  public void processAndSaveAllRestaurants() {
    try {
      if (webPageDownloader.isDownloadOverrideOrDataExpired()) {
        log.info("event=file_download_and_processing message=\"Beginning file download process\"");
        webPageDownloader.initiateDownloadsAndProcessFiles();
      } else {
        log.info("event=file_processing message=\"File download not needed. Processing existing files\"");
        webPageProcessing.startProcessingOfDownloadedFiles();
      }
    } catch (Exception e) {
      log.error("Exception in processAndSaveAllRestaurants", e);
    }
  }
}
