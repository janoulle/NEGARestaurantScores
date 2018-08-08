package com.janeullah.healthinspectionrecords.services.internal;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;

import java.util.List;

public interface RestaurantService {
    List<FlattenedRestaurant> findAllFlattenedRestaurants();

    List<Restaurant> findAll();

    List<Restaurant> findByEstablishmentInfoCountyIgnoreCase(String county);

    List<Restaurant> findByEstablishmentInfoNameContaining(String name);

    List<Restaurant> findEstablishmentByNameAndCounty(String name, String county);

    Restaurant findById(long id);

    List<Restaurant> findRestaurantsWithScoresGreaterThanOrEqual(int score);

    List<Restaurant> findRestaurantsWithScoresLessThanOrEqual(int score);

    List<Restaurant> findRestaurantsWithScoresBetween(int lower, int upper);

    List<Restaurant> findRestaurantsWithCriticalViolations();

    void saveAll(List<Restaurant> restaurants);

    void deleteAllRecords();

    long getCount();
}
