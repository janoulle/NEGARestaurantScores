package com.janeullah.healthinspectionrecords.services.impl;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.services.ElasticSearchable;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertFalse;

public class ElasticSearchableTest {

    @Test
    public void testHandleProcessingOfData() {
        ElasticSearchable<String> test = new ElasticSearchable<String>() {
            @Override
            public ResponseEntity<String> addRestaurantDocument(Long id, FlattenedRestaurant restaurant) {
                return null;
            }

            @Override
            public ResponseEntity<String> addRestaurantDocuments(List<FlattenedRestaurant> flattenedRestaurants) {
                return null;
            }
        };

        assertFalse(test.handleProcessingOfData());
    }
}
