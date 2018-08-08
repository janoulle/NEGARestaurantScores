package com.janeullah.healthinspectionrecords.services.internal;

import com.janeullah.healthinspectionrecords.constants.CacheConstants;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    @Cacheable(value = CacheConstants.ALL_FLATTENED_RESTAURANTS)
    public List<FlattenedRestaurant> findAllFlattenedRestaurants() {
        return restaurantRepository.findAllFlattenedRestaurants();
    }

    @Override
    @Cacheable(value = CacheConstants.ALL_RESTAURANTS)
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    @Override
    @Cacheable(value = CacheConstants.RESTAURANTS_BY_COUNTY, key = "#county")
    public List<Restaurant> findByEstablishmentInfoCountyIgnoreCase(String county) {
        return restaurantRepository.findByEstablishmentInfoCountyIgnoreCase(county);
    }

    @Override
    @Cacheable(value = CacheConstants.RESTAURANTS_CONTAINING_NAME, key = "#name")
    public List<Restaurant> findByEstablishmentInfoNameContaining(String name) {
        return restaurantRepository.findByEstablishmentInfoNameContaining(name);
    }

    @Override
    @Cacheable(value = CacheConstants.RESTAURANTS_BY_NAME_AND_COUNTY, key = "#name + '-' + #county")
    public List<Restaurant> findEstablishmentByNameAndCounty(String name, String county) {
        return restaurantRepository.findByEstablishmentInfoNameIgnoreCaseAndEstablishmentInfoCountyIgnoreCase(name, county);
    }

    @Override
    @Cacheable(value = CacheConstants.RESTAURANT_BY_ID, key = "#id", unless = "#result != null")
    public Restaurant findById(long id) {
        Optional<Restaurant> found = restaurantRepository.findById(id);
        return found.orElse(null);
    }

    @Override
    @Cacheable(value = CacheConstants.RESTAURANTS_WITH_SCORES_GREATER_THAN, key = "#score")
    public List<Restaurant> findRestaurantsWithScoresGreaterThanOrEqual(int score) {
        return restaurantRepository.findRestaurantsWithScoresGreaterThanOrEqual(score);
    }

    @Override
    @Cacheable(value = CacheConstants.RESTAURANTS_WITH_SCORES_LESSER_THAN, key = "#score")
    public List<Restaurant> findRestaurantsWithScoresLessThanOrEqual(int score) {
        return restaurantRepository.findRestaurantsWithScoresLessThanOrEqual(score);
    }

    @Override
    @Cacheable(value = CacheConstants.RESTAURANTS_WITH_SCORES_BETWEEN, key = "#lower + '-' + #upper")
    public List<Restaurant> findRestaurantsWithScoresBetween(int lower, int upper) {
        return restaurantRepository.findRestaurantsWithScoresBetween(lower, upper);
    }

    @Override
    @Cacheable(value = CacheConstants.RESTAURANTS_WITH_CRITICAL_VIOLATIONS)
    public List<Restaurant> findRestaurantsWithCriticalViolations() {
        return restaurantRepository.findRestaurantsWithCriticalViolations();
    }

    @Override
    @Transactional
    public void saveAll(List<Restaurant> restaurants) {
        restaurantRepository.saveAll(restaurants);
    }

    @Override
    public void deleteAllRecords() {
        restaurantRepository.deleteAll();
    }

    @Override
    public long getCount() {
        return restaurantRepository.count();
    }
}
