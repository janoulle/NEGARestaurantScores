package com.janeullah.healthinspectionrecords.org.controller;

import com.janeullah.healthinspectionrecords.org.web.WebEventOrchestrator;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: jane
 * Date:  9/18/2016
 */
@RestController
public class WebPageController {
    private static final Logger logger = Logger.getLogger(WebPageController.class);


    @RequestMapping(value = "/data/refreshPages",method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void redownloadPages(){
        logger.info("event=\"Refreshing pages from\"");
        System.setProperty("DOWNLOAD_OVERRIDE","true");
        System.setProperty("SET_WATCHER","true");
        WebEventOrchestrator orchestrator = new WebEventOrchestrator();
        orchestrator.run();
    }

}
