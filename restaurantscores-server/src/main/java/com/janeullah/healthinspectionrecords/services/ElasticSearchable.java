package com.janeullah.healthinspectionrecords.services;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Author: Jane Ullah Date: 10/21/2017
 */
public interface ElasticSearchable<T> {

    ResponseEntity<T> addRestaurantDocument(Long id, FlattenedRestaurant restaurant);

    default ResponseEntity<T> addRestaurantDocuments(List<FlattenedRestaurant> flattenedRestaurants) {
        for (FlattenedRestaurant flattenedRestaurant : flattenedRestaurants) {
            ResponseEntity<T> status =
                    addRestaurantDocument(flattenedRestaurant.getId(), flattenedRestaurant);
            // TODO: figure out best way to report errors
            if (!status.getStatusCode().is2xxSuccessful()) {
                LogHolder.LOGGER.error(
                        "Failed to write data about restaurant={} to the db with response={} and statusCode={}",
                        flattenedRestaurant,
                        status.getBody(),
                        status.getStatusCode());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

// https://stackoverflow.com/questions/28720845/logging-from-default-interface-methods
final class LogHolder {
    static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchable.class);

    private LogHolder() {
    }
}