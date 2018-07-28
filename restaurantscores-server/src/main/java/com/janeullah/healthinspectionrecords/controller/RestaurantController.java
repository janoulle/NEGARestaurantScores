package com.janeullah.healthinspectionrecords.controller;

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
import java.util.Optional;

/**
 * Author: Jane Ullah Date: 9/17/2016
 */

/**
 * Methods in this restcontroller are invoked by whomever is running the program.
 */
@SuppressWarnings("unused")
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
    @GetMapping(
            value = "/allFlattened",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<FlattenedRestaurant> fetchAllFlattened() {
        return restaurantRepository.findAllFlattenedRestaurants();
    }

    @Cacheable("allRestaurants")
    @GetMapping(
            value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> fetchAll() {
        return restaurantRepository.findAll();
    }

    @Cacheable("restaurantsByCounty")
    @GetMapping(
            value = "/county/{county}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantByCounty(@PathVariable("county") @NotNull String county) {
        log.info("finding restaurant by county {}", county);
        return restaurantRepository.findByEstablishmentInfoCountyIgnoreCase(county);
    }

    @Cacheable("restaurantsByName")
    @GetMapping(
            value = "/name/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantByName(@PathVariable("name") @NotNull String name) {
        log.info("finding restaurant by name {}", name);
        return restaurantRepository.findByEstablishmentInfoNameIgnoreCase(name);
    }

    @Cacheable("restaurantsContainingName")
    @GetMapping(
            value = "/contains/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantLikeName(@PathVariable("name") @NotNull String name) {
        log.info("finding restaurant by name {}", name);
        return restaurantRepository.findByEstablishmentInfoNameContaining(name);
    }

    @Cacheable("restaurantsByNameAndCounty")
    @GetMapping(
            value = "/search/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantByNameAndCounty(
            @RequestHeader("name") String name, @RequestHeader("county") String county) {
        log.info("finding restaurant by name {} and county {}", name, county);
        return restaurantRepository.findByEstablishmentInfoNameIgnoreCaseAndEstablishmentInfoCountyIgnoreCase(
                name, county);
    }

    @Cacheable("restaurantById")
    @GetMapping(
            value = "/id/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Restaurant getRestaurantById(@PathVariable("id") long id) {
        log.info("getting restaurant id {}", id);
        Optional<Restaurant> found = restaurantRepository.findById(id);
        return found.orElse(null);
    }

    @Cacheable("scoresGreaterThan")
    @GetMapping(
            value = "/score/greaterorequal/{score}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantsWithScoresGreaterThanOrEqual(
            @PathVariable("score") int score) {
        return restaurantRepository.findRestaurantsWithScoresGreaterThanOrEqual(score);
    }

    @Cacheable("scoresLesserThan")
    @GetMapping(
            value = "/score/lesserorequal/{score}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantsWithScoresLessThanOrEqual(
            @PathVariable("score") int score) {
        return restaurantRepository.findRestaurantsWithScoresLessThanOrEqual(score);
    }

    @Cacheable("scoresBetweenRange")
    @GetMapping(
            value = "/score/lower={lower}&upper={upper}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantsWithScoresGreaterThanOrEqual(
            @PathVariable("lower") int lower, @PathVariable("upper") int upper) {
        return restaurantRepository.findRestaurantsWithScoresBetween(lower, upper);
    }

    @Cacheable("restaurantsWithCriticalViolations")
    @GetMapping(
            value = "/violations/critical",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Restaurant> getRestaurantsWithCriticalViolations() {
        return restaurantRepository.findRestaurantsWithCriticalViolations();
    }
}
