package com.janeullah.healthinspectionrecords.org.controller;

import com.janeullah.healthinspectionrecords.org.model.Restaurant;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: jane
 * Date:  9/18/2016
 */
@RestController
public class WebPageController {
    private static final Logger logger = Logger.getLogger(WebPageController.class);


    @RequestMapping(value = "/updatePagesOnDisk",method = RequestMethod.GET)
    public List<Restaurant> all(){
        logger.info("event=\"Refreshing pages from\"");
        return new ArrayList<>();
    }

}
