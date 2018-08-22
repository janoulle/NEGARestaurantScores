package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.services.internal.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Author: Jane Ullah Date: 9/17/2016
 */
@SuppressWarnings("unused")
@Slf4j
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }


    @GetMapping(
            value = "/allFlattened",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<FlattenedRestaurant> fetchAllFlattened() {
        return restaurantService.findAllFlattenedRestaurants();
    }

    @GetMapping(
            value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> fetchAll() {
        return restaurantService.findAll();
    }

    @GetMapping(
            value = "/county/{county}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantByCounty(@PathVariable("county") @NotNull String county) {
        log.info("finding restaurant by county {}", county);
        return restaurantService.findByEstablishmentInfoCountyIgnoreCase(county);
    }

    @GetMapping(
            value = "/contains/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantLikeName(@PathVariable("name") @NotNull String name) {
        log.info("finding restaurant by name {}", name);
        return restaurantService.findByEstablishmentInfoNameContaining(name);
    }

    @GetMapping(
            value = "/search/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantByNameAndCounty(
            @RequestHeader("name") String name, @RequestHeader("county") String county) {
        log.info("finding restaurant by name {} and county {}", name, county);
        return restaurantService.findEstablishmentByNameAndCounty(
                name, county);
    }

    @GetMapping(
            value = "/id/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Restaurant getRestaurantById(@PathVariable("id") long id) {
        log.info("getting restaurant id {}", id);
        return restaurantService.findById(id);
    }

    @GetMapping(
            value = "/score/greaterorequal/{score}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantsWithScoresGreaterThanOrEqual(
            @PathVariable("score") int score) {
        return restaurantService.findRestaurantsWithScoresGreaterThanOrEqual(score);
    }

    @GetMapping(
            value = "/score/lesserorequal/{score}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantsWithScoresLessThanOrEqual(
            @PathVariable("score") int score) {
        return restaurantService.findRestaurantsWithScoresLessThanOrEqual(score);
    }

    @GetMapping(
            value = "/score/lower={lower}&upper={upper}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantsWithScoresGreaterThanOrEqual(
            @PathVariable("lower") int lower, @PathVariable("upper") int upper) {
        return restaurantService.findRestaurantsWithScoresBetween(lower, upper);
    }

    @GetMapping(
            value = "/violations/critical",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantsWithCriticalViolations() {
        return restaurantService.findRestaurantsWithCriticalViolations();
    }
}
