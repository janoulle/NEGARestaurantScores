package com.janeullah.healthinspectionrecords.services;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Author: Jane Ullah Date: 10/21/2017
 */
public interface ElasticSearchable<T> {

    default boolean handleProcessingOfData() {
        return false;
    }

    ResponseEntity<T> addRestaurantDocument(Long id, FlattenedRestaurant restaurant);

    ResponseEntity<T> addRestaurantDocuments(List<FlattenedRestaurant> flattenedRestaurants);
}
