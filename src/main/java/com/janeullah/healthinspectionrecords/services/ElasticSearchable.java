package com.janeullah.healthinspectionrecords.services;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import org.springframework.http.ResponseEntity;

/**
 * Author: jane
 * Date:  10/21/2017
 */
public interface ElasticSearchable<T> {
    ResponseEntity<T> addRestaurantDocument(Long id, FlattenedRestaurant restaurant);
}
