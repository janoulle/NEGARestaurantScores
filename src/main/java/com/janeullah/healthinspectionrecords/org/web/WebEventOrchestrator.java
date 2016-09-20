package com.janeullah.healthinspectionrecords.org.web;

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
        webPageDownloader.executeProcess();
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

    public List<Restaurant> getAllRestaurants(){
        try {
            webPageProcessing.executeProcess();
            List<Restaurant> result = new ArrayList<>();
            ConcurrentMap<String, List<Restaurant>> restaurants = WebPageProcessing.getRestaurantsByCounties();
            restaurants.entrySet().forEach(entry ->
                    result.addAll(entry.getValue())
            );
            getSignalForProcessing().await();
            return result;
        }catch (InterruptedException e){
            logger.error(e);
        }
        return new ArrayList<>();
    }

//    public static void main(String[] args){
//        WebEventOrchestrator orch = new WebEventOrchestrator();
//        orch.run();
//    }
}
