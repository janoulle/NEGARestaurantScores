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
    private final static Logger logger = LoggerFactory.getLogger(WebEventOrchestrator.class);

    @Autowired
    WebPageDownloader webPageDownloader;

    @Autowired
    WebPageProcessing webPageProcessing;

    public void processAndSaveAllRestaurants(){
        try {
            if (WebPageDownloader.isDataExpired()){
                webPageDownloader.initiateDownloadsAndProcessFiles();
            }else {
                webPageProcessing.startProcessingOfDownloadedFiles();
            }
        }catch (Exception e){
            logger.error("Exception in getAndSaveAllRestaurants",e);
        }
    }
}
