package com.janeullah.healthinspectionrecords.services.impl;

import com.google.common.collect.ImmutableMap;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.rest.RemoteRestClient;
import com.janeullah.healthinspectionrecords.services.ElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.ElasticSearchable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Author: jane
 * Date:  10/21/2017
 */
@Slf4j
@Service
public class LocalhostElasticSearchDocumentService extends ElasticSearchDocumentService implements ElasticSearchable {
    private static final String LOCALHOST_ELASTICSEARCH_TYPE_URL = "http://localhost:9200/restaurants/restaurant/{id}";
    private static final String LOCALHOST_ELASTICSEARCH_INDEX_URL = "http://localhost:9200/restaurants";

    public LocalhostElasticSearchDocumentService() {
    }

    @Autowired
    public LocalhostElasticSearchDocumentService(RemoteRestClient restClient) {
        super(restClient);
    }

    //replace pathvariable with map value
    @Override
    public ResponseEntity<String> addRestaurantDocument(Long id, FlattenedRestaurant flattenedRestaurant) {
        Map<String, Long> vars = ImmutableMap.of("id", id);
        return restClient.getRestTemplate().postForEntity(LOCALHOST_ELASTICSEARCH_TYPE_URL, flattenedRestaurant, String.class, vars);
    }
}
