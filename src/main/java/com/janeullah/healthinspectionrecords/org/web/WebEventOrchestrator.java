package com.janeullah.healthinspectionrecords.org.web;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebEventOrchestrator {
    private WebPageDownloader webPageDownloader = new WebPageDownloader();
    private WebPageProcessing webPageProcessing = new WebPageProcessing();

    public void executeProcess(){
        webPageDownloader.executeProcess();
        webPageProcessing.executeProcess();
    }

    public static void main(String[] args){
        WebEventOrchestrator orchestrator = new WebEventOrchestrator();
        orchestrator.executeProcess();
    }
}
