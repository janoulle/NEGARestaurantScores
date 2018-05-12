package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.repository.ViolationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Author: Jane Ullah Date: 9/20/2016 */
/**
 * Methods in this restcontroller are invoked by whomever is running the program.
 */
@SuppressWarnings("unused")
@Slf4j
@RestController
@RequestMapping("/violations")
public class ViolationsController {
  private ViolationRepository violationRepository;

  @Autowired
  public ViolationsController(ViolationRepository violationRepository) {
    this.violationRepository = violationRepository;
  }

  @Cacheable("violationsById")
  @RequestMapping(
    value = "/id/{id}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Violation getViolationById(@PathVariable("id") long id) {
    log.info("getting violation id {}", id);
    return violationRepository.findById(id).orElse(null);
  }

  @Cacheable("violationsByCategory")
  @RequestMapping(
    value = "/category/{category}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Violation> getViolationsByCode(@PathVariable("category") String category) {
    log.info("getting violations by code {}", category);
    return violationRepository.findByCategory(category);
  }

  @Cacheable("violationsByRestaurant")
  @RequestMapping(
    value = "/restaurantId/{id}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Violation> findViolationsByRestaurantId(@PathVariable("id") Long id) {
    return violationRepository.findViolationsByRestaurantId(id);
  }
}
