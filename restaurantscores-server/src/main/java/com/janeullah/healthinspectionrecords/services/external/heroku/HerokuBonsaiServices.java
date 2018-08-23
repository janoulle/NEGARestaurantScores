package com.janeullah.healthinspectionrecords.services.external.heroku;

import com.janeullah.healthinspectionrecords.config.feign.HerokuBonsaiFeignConfiguration;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.dtos.heroku.Acknowledgement;
import com.janeullah.healthinspectionrecords.domain.dtos.heroku.HerokuIndexResponse;
import com.janeullah.healthinspectionrecords.exceptions.HerokuClientException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
 * https://dzone.com/articles/rest-api-testing-with-spring-cloud-feign-clients
 * https://github.com/OpenFeign/feign/blob/master/core/src/main/java/feign/QueryMap.java
 * https://cloud.spring.io/spring-cloud-openfeign/single/spring-cloud-openfeign.html#netflix-feign-starter
 */
@FeignClient(name = "herokuBonsaiServices", url = "${BONSAI_URL}", configuration = HerokuBonsaiFeignConfiguration.class)
public interface HerokuBonsaiServices {

    @GetMapping(value = "/restaurants")
    HerokuIndexResponse getRestaurantIndex(@RequestHeader Map<String, String> headerMap) throws HerokuClientException;

    @DeleteMapping(value = "/restaurants")
    Acknowledgement deleteRestaurantsIndex(@RequestHeader Map<String, String> headerMap) throws HerokuClientException;

    @PutMapping(value = "/restaurants")
    Acknowledgement createRestaurantsIndex(@RequestHeader Map<String, String> headerMap) throws HerokuClientException;

    @PostMapping(value = "/restaurants/restaurant/{id}")
    Acknowledgement addRestaurantToIndex(@PathVariable(value = "id") String restaurantId,
                                         @RequestHeader Map<String, String> headerMap,
                                         @RequestBody FlattenedRestaurant restaurant) throws HerokuClientException;
}
