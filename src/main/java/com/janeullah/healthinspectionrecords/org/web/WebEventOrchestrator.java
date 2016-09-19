package com.janeullah.healthinspectionrecords.org.web;

import com.janeullah.healthinspectionrecords.org.util.ExecutorUtil;

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

    public static void main(String[] args){
        WebEventOrchestrator orch = new WebEventOrchestrator();
        orch.run();
    }
}
