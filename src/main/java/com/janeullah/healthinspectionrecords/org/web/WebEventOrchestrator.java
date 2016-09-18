package com.janeullah.healthinspectionrecords.org.web;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebEventOrchestrator {
    private WebPageDownloader webPageDownloader = new WebPageDownloader();
    private WebPageProcessing webPageProcessing = new WebPageProcessing();

    public WebPageDownloader getWebPageDownloader() {
        return webPageDownloader;
    }

    public void setWebPageDownloader(WebPageDownloader webPageDownloader) {
        this.webPageDownloader = webPageDownloader;
    }

    public WebPageProcessing getWebPageProcessing() {
        return webPageProcessing;
    }

    public void setWebPageProcessing(WebPageProcessing webPageProcessing) {
        this.webPageProcessing = webPageProcessing;
    }

    public void executeProcess(){
        webPageProcessing.executeProcess();
        webPageDownloader.executeProcess();
    }

    public static void main(String[] args){
        WebEventOrchestrator orchestrator = new WebEventOrchestrator();
        orchestrator.executeProcess();
    }
}
