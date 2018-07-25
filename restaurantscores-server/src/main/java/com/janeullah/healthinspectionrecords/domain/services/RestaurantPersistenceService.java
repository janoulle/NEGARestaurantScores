package com.janeullah.healthinspectionrecords.domain.services;


import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Transactional
@Service
public class RestaurantPersistenceService {

    private RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantPersistenceService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public void saveAll(List<Restaurant> restaurants) {
        restaurantRepository.saveAll(restaurants);
    }
}
