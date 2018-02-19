package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.repository.ViolationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: Jane Ullah
 * Date:  9/20/2016
 */
@RestController
@RequestMapping("/violations")
public class ViolationsController {
    private static final Logger logger = LoggerFactory.getLogger(ViolationsController.class);

    @Autowired
    private ViolationRepository violationRepository;

    @Cacheable("violationsById")
    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Violation getViolationById(@PathVariable("id") long id) {
        logger.info("getting violation id {}", id);
        return violationRepository.findOne(id);
    }

    @Cacheable("violationsByCategory")
    @RequestMapping(value = "/category/{category}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Violation> getViolationsByCode(@PathVariable("category") String category) {
        logger.info("getting violations by code {}", category);
        return violationRepository.findByCategory(category);
    }

    @Cacheable("violationsByRestaurant")
    @RequestMapping(value = "/restaurantId/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Violation> findViolationsByRestaurantId(@PathVariable("id") Long id) {
        return violationRepository.findViolationsByRestaurantId(id);
    }
}
