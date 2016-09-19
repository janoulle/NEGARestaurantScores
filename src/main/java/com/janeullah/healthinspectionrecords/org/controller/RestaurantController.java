package com.janeullah.healthinspectionrecords.org.controller;

import com.janeullah.healthinspectionrecords.org.constants.InspectionType;
import com.janeullah.healthinspectionrecords.org.constants.Severity;
import com.janeullah.healthinspectionrecords.org.model.EstablishmentInfo;
import com.janeullah.healthinspectionrecords.org.model.InspectionReport;
import com.janeullah.healthinspectionrecords.org.model.Restaurant;
import com.janeullah.healthinspectionrecords.org.model.Violation;
import com.janeullah.healthinspectionrecords.org.web.WebEventOrchestrator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Author: jane
 * Date:  9/17/2016
 */
@RestController
public class RestaurantController {
    private static final Logger logger = Logger.getLogger(RestaurantController.class);

    @RequestMapping(value = "/restaurants",method = {RequestMethod.GET})
    public List<Restaurant> all(){
        logger.info("getting all restaurants");
        WebEventOrchestrator orchestrator = new WebEventOrchestrator();
        return orchestrator.getAllRestaurants();
    }

    @RequestMapping(value = "/restaurant/id/{id}",method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public Restaurant getRestaurantById(@PathVariable("id") int id){
        logger.info("getting restaurant id " + id);

        Violation violation = new Violation("17C", StringUtils.EMPTY, StringUtils.EMPTY, Severity.CRITICAL);
        InspectionReport report = new InspectionReport(91,"12/17/2015", InspectionType.ROUTINE);
        report.setViolations(Collections.singletonList(violation));
        Restaurant restaurant = new Restaurant();
        restaurant.setEstablishmentInfo(new EstablishmentInfo());
        restaurant.setInspectionReports(Collections.singletonList(report));
        return restaurant;
    }
}
