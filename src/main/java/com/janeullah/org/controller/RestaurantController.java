package com.janeullah.org.controller;

import com.janeullah.org.model.InspectionReport;
import com.janeullah.org.model.Restaurant;
import com.janeullah.org.model.Violation;
import com.janeullah.org.util.constants.InspectionType;
import com.janeullah.org.util.constants.Severity;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: jane
 * Date:  9/17/2016
 */
@RestController
public class RestaurantController {
    private static final Logger logger = Logger.getLogger(RestaurantController.class);

    @RequestMapping(value = "/all",method = RequestMethod.GET)
    public List<Restaurant> all(){
        logger.info("getting all restaurants");
        return new ArrayList<>();
    }

    @RequestMapping(value = "/restaurant",method = RequestMethod.GET)
    public Restaurant getRestaurantById(@RequestParam(value="id") long id){
        logger.info("getting restaurant id " + id);

        Violation violation = new Violation("17C", StringUtils.EMPTY, StringUtils.EMPTY, Severity.CRITICAL);
        InspectionReport report = new InspectionReport(DateTime.now(), InspectionType.ROUTINE);
        report.setViolations(Collections.singletonList(violation));
        Restaurant restaurant = new Restaurant("364 E Broad St Athens GA, 30601");
        restaurant.setInspectionReports(Collections.singletonList(report));
        return restaurant;
    }
}
