package com.janeullah.healthinspectionrecords.services;

import com.google.common.collect.ImmutableMap;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * Author: jane
 * Date:  9/23/2017
 */
@Service
public class ElasticSearchDocumentService {
    private static final RestTemplate restTemplate = new RestTemplate();
    //private static final String herokuUrlTemplate = System.getenv("BONSAI_URL");
    private static final String baseUrlTemplate = "http://localhost:9200/restaurants/restaurant/{id}";
    private static final String deleteRestaurantIndexUrl = "http://localhost:9200/restaurants";

    public ResponseEntity<String> addDocument(Long id, FlattenedRestaurant flattenedRestaurant) {
        Map<String, Long> vars = ImmutableMap.of("id",id);
        return restTemplate.postForEntity(baseUrlTemplate, flattenedRestaurant, String.class, vars);
    }

    public ResponseEntity<HttpStatus> deleteRestaurantIndex(){
        try {
            restTemplate.delete(new URI(deleteRestaurantIndexUrl));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            throw new IllegalArgumentException(e);
        }
    }
}
