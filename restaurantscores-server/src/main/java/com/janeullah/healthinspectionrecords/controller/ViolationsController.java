package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.services.internal.ViolationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: Jane Ullah Date: 9/20/2016
 */
@SuppressWarnings("unused")
@Slf4j
@RestController
@RequestMapping("/violations")
public class ViolationsController {
    private ViolationService violationService;

    @Autowired
    public ViolationsController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping(
            value = "/id/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Violation getViolationById(@PathVariable("id") long id) {
        log.info("getting violation id {}", id);
        return violationService.findById(id);
    }

    @GetMapping(
            value = "/category/{category}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Violation> getViolationsByCode(@PathVariable("category") String category) {
        log.info("getting violations by code {}", category);
        return violationService.findByCategory(category);
    }

    @GetMapping(
            value = "/restaurantId/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Violation> findViolationsByRestaurantId(@PathVariable("id") Long id) {
        return violationService.findViolationsByRestaurantId(id);
    }

    @GetMapping(
            value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Violation> fetchAll() {
        return violationService.findAll();
    }
}
