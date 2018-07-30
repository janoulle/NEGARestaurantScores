package com.janeullah.healthinspectionrecords.services.impl;

import com.google.common.collect.ImmutableMap;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.dtos.heroku.HerokuIndexResponse;
import com.janeullah.healthinspectionrecords.exceptions.HerokuClientException;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.services.ElasticSearchable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HerokuBonsaiElasticSearchDocumentService implements ElasticSearchable<HttpStatus> {

    @Value("${BONSAI_USERNAME}")
    private String herokuBonsaiUserName;

    @Value("${BONSAI_PASSWORD}")
    private String herokuBonsaiPassword;

    private Map<String, String> httpHeaders = getAuthHeaders();

    private HerokuBonsaiServices herokuBonsaiServices;
    private RestaurantRepository restaurantRepository;

    @Autowired
    public HerokuBonsaiElasticSearchDocumentService(HerokuBonsaiServices herokuBonsaiServices,
                                                    RestaurantRepository restaurantRepository) {
        this.herokuBonsaiServices = herokuBonsaiServices;
        this.restaurantRepository = restaurantRepository;
    }

    public boolean handleProcessingOfData() {
        List<FlattenedRestaurant> flattenedRestaurants =
                restaurantRepository.findAllFlattenedRestaurants();
        ResponseEntity<HttpStatus> result = addRestaurantDocuments(flattenedRestaurants);
        return result.getStatusCode().is2xxSuccessful();
    }

    @Override
    public ResponseEntity<HttpStatus> addRestaurantDocuments(List<FlattenedRestaurant> flattenedRestaurants) {

        if (isIndexSetupOkay()) {
            for (FlattenedRestaurant flattenedRestaurant : flattenedRestaurants) {
                ResponseEntity<HttpStatus> status =
                        addRestaurantDocument(flattenedRestaurant.getId(), flattenedRestaurant);
                if (!status.getStatusCode().is2xxSuccessful()) {
                    log.error(
                            "Failed to write data about restaurant={} to Heroku", flattenedRestaurant);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> addRestaurantDocument(
            Long id, FlattenedRestaurant flattenedRestaurant) {
        try {
            herokuBonsaiServices.addRestaurantToIndex(String.valueOf(id), httpHeaders, flattenedRestaurant);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (HerokuClientException e) {
            log.error("Error saving restaurantId={} to Heroku errorType={}", id, e.getErrorType(), e);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostConstruct
    public Map<String, String> getAuthHeaders() {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            String base64EncodedValue =
                    new String(
                            encoder.encode(
                                    (herokuBonsaiUserName + ":" + herokuBonsaiPassword).getBytes("UTF-8")));
            this.httpHeaders = ImmutableMap.of("Authorization", "Basic " + base64EncodedValue);
        } catch (Exception e) {
            log.error("Error generating auth header", e);
            this.httpHeaders = new HashMap<>();
        }
        return this.httpHeaders;
    }

    public boolean isIndexSetupOkay() {
        try {
            if (isIndexPresent()) {
                // delete index first and then create api
                herokuBonsaiServices.deleteRestaurantsIndex(httpHeaders);
            }
            // okay to call create Index api
            herokuBonsaiServices.createRestaurantsIndex(httpHeaders);
            return true;
        } catch (Exception e) {
            log.error("Unable to setup Heroku Bonsai service", e);
        }
        return false;
    }

    public boolean isIndexPresent() {
        String errorType;
        try {
            HerokuIndexResponse index = herokuBonsaiServices.getRestaurantIndex(httpHeaders);

            return index != null && index.getRestaurants() != null;
        } catch (HerokuClientException e) {
            log.error(e.getErrorType(), e);
            errorType = e.getErrorType();
        }
        return !"index_not_found_exception".equalsIgnoreCase(errorType);
    }
}
