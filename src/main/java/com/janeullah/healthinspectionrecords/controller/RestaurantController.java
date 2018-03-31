package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/** Author: Jane Ullah Date: 9/17/2016 */
@Slf4j
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
  private RestaurantRepository restaurantRepository;

  @Autowired
  public RestaurantController(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  @Cacheable("allFlattenedRestaurants")
  @RequestMapping(
    value = "/allFlattened",
    method = {RequestMethod.GET},
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Iterable<FlattenedRestaurant> fetchAllFlattened() {
    return restaurantRepository.findAllFlattenedRestaurants();
  }

  @Cacheable("allRestaurants")
  @RequestMapping(
    value = "/all",
    method = {RequestMethod.GET},
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Iterable<Restaurant> fetchAll() {
    return restaurantRepository.findAll();
  }

  @RequestMapping(
    value = "/countylist",
    method = {RequestMethod.GET},
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<String> fetchCounties() {
    return WebPageConstants.COUNTY_LIST;
  }

  @Cacheable("restaurantsByCounty")
  @RequestMapping(
    value = "/county/{county}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Restaurant> getRestaurantByCounty(@PathVariable("county") @NotNull String county) {
    log.info("finding restaurant by county {}", county);
    return restaurantRepository.findByEstablishmentInfoCountyIgnoreCase(county);
  }

  @Cacheable("restaurantsByName")
  @RequestMapping(
    value = "/name/{name}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Restaurant> getRestaurantByName(@PathVariable("name") @NotNull String name) {
    log.info("finding restaurant by name {}", name);
    return restaurantRepository.findByEstablishmentInfoNameIgnoreCase(name);
  }

  @Cacheable("restaurantsContainingName")
  @RequestMapping(
    value = "/contains/{name}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Restaurant> getRestaurantLikeName(@PathVariable("name") @NotNull String name) {
    log.info("finding restaurant by name {}", name);
    return restaurantRepository.findByEstablishmentInfoNameContaining(name);
  }

  @Cacheable("restaurantsByNameAndCounty")
  @RequestMapping(
    value = "/search/",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Restaurant> getRestaurantByNameAndCounty(
      @RequestHeader("name") String name, @RequestHeader("county") String county) {
    log.info("finding restaurant by name {} and county {}", name, county);
    return restaurantRepository.findByEstablishmentInfoNameAndEstablishmentInfoCountyIgnoreCase(
        name, county);
  }

  @Cacheable("restaurantById")
  @RequestMapping(
    value = "/id/{id}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Restaurant getRestaurantById(@PathVariable("id") long id) {
    log.info("getting restaurant id {}", id);
    return restaurantRepository.findOne(id);
  }

  @Cacheable("scoresGreaterThan")
  @RequestMapping(
    value = "/score/greaterorequal/{score}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Restaurant> getRestaurantsWithScoresGreaterThanOrEqual(
      @PathVariable("score") int score) {
    return restaurantRepository.findRestaurantsWithScoresGreaterThanOrEqual(score);
  }

  @Cacheable("scoresLesserThan")
  @RequestMapping(
    value = "/score/lesserorequal/{score}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Restaurant> getRestaurantsWithScoresLessThanOrEqual(
      @PathVariable("score") int score) {
    return restaurantRepository.findRestaurantsWithScoresLessThanOrEqual(score);
  }

  @Cacheable("scoresBetweenRange")
  @RequestMapping(
    value = "/score/lower={lower}&upper={upper}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Restaurant> getRestaurantsWithScoresGreaterThanOrEqual(
      @PathVariable("lower") int lower, @PathVariable("upper") int upper) {
    return restaurantRepository.findRestaurantsWithScoresBetween(lower, upper);
  }

  @Cacheable("restaurantsWithCriticalViolations")
  @RequestMapping(
    value = "/violations/critical",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Restaurant> getRestaurantsWithCriticalViolations() {
    return restaurantRepository.findRestaurantsWithCriticalViolations();
  }
}
