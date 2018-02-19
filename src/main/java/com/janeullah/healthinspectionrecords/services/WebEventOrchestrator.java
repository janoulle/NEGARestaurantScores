package com.janeullah.healthinspectionrecords.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Jane Ullah
 * Date:  9/18/2016
 */
@Component
public class WebEventOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(WebEventOrchestrator.class);
    private WebPageDownloader webPageDownloader;
    private WebPageProcessing webPageProcessing;

    @Autowired
    public WebEventOrchestrator(WebPageDownloader webPageDownloader,
                                WebPageProcessing webPageProcessing) {
        this.webPageDownloader = webPageDownloader;
        this.webPageProcessing = webPageProcessing;
    }

    public void processAndSaveAllRestaurants() {
        try {
            if (WebPageDownloader.isDataExpired()) {
                webPageDownloader.initiateDownloadsAndProcessFiles();
            } else {
                webPageProcessing.startProcessingOfDownloadedFiles();
            }
        } catch (Exception e) {
            logger.error("Exception in processAndSaveAllRestaurants", e);
        }
    }
}
