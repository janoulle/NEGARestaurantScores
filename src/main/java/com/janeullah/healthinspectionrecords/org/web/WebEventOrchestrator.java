package com.janeullah.healthinspectionrecords.org.web;

import com.janeullah.healthinspectionrecords.org.model.Restaurant;
import com.janeullah.healthinspectionrecords.org.util.ExecutorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebEventOrchestrator {
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

    public List<Restaurant> getAllRestaurants(){
        webPageProcessing.executeProcess();
        List<Restaurant> result = new ArrayList<>();
        ConcurrentMap<String,List<Restaurant>> restaurants = WebPageProcessing.getRestaurantsByCounties();
        restaurants.entrySet().forEach(entry ->
                result.addAll(entry.getValue())
        );
        return result;
    }

    public static void main(String[] args){
        WebEventOrchestrator orch = new WebEventOrchestrator();
        orch.run();
    }
}
