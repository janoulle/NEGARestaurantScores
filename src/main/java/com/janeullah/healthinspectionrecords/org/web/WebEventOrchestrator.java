package com.janeullah.healthinspectionrecords.org.web;

import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.org.exceptions.config.IncompatibleConfigurationException;
import com.janeullah.healthinspectionrecords.org.model.Restaurant;
import com.janeullah.healthinspectionrecords.org.util.ExecutorUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebEventOrchestrator {
    private final static Logger logger = Logger.getLogger(WebEventOrchestrator.class);
    private WebPageDownloader webPageDownloader = new WebPageDownloader();
    private WebPageProcessing webPageProcessing = new WebPageProcessing();

    private void executeProcess() {
        logger.info("event=\"tasks for downloading webpage kicked off\"");
        boolean downloadKickedOff = webPageDownloader.executeProcess();
        if (downloadKickedOff){
            try {
                logger.info("event=\"waiting for threads to complete downloading\"");
                webPageDownloader.getDoneSignal().await();
                logger.info("event=\"await completed for threads involved in downloading\"");
            }catch (InterruptedException e){
                logger.error(e);
            }
        }
        logger.info("event=\"tasks for processing downloaded webpages kicked off\"");
        webPageProcessing.executeProcess();
    }

    private void shutDownExecutor() {
        ExecutorUtil.shutDown();
    }

    public void run() {
        executeProcess();
        shutDownExecutor();
    }

    public CountDownLatch getSignalForProcessing(){
        return webPageProcessing.getDoneSignal();
    }

    public CountDownLatch getSignalForDownloads(){
        return webPageDownloader.getDoneSignal();
    }

    public List<Restaurant> getAllRestaurants(){
        try {
            if (WebPageConstants.SET_WATCHER){
                throw new IncompatibleConfigurationException("SET_WATCHER must always be false");
            }

            executeProcess();

            logger.info("event=\"waiting for threads to complete processing\"");
            getSignalForProcessing().await();
            logger.info("event=\"await completed for threads involved in processing\"");

            //shut down
            shutDownExecutor();

            List<Restaurant> result = new ArrayList<>();
            ConcurrentMap<String, List<Restaurant>> restaurants = WebPageProcessing.getRestaurantsByCounties();
            restaurants.entrySet().forEach(entry ->
                    result.addAll(entry.getValue())
            );
            return result;
        }catch (IncompatibleConfigurationException | InterruptedException e){
            logger.error(e);
        }
        return new ArrayList<>();
    }

//    public static void main(String[] args){
//        WebEventOrchestrator orch = new WebEventOrchestrator();
//        orch.run();
//    }
}
