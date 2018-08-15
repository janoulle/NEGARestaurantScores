package com.janeullah.healthinspectionrecords.constants;

public class CacheConstants {
    public static final String ALL_RESTAURANTS = "allRestaurants";
    public static final String ALL_FLATTENED_RESTAURANTS = "allFlattenedRestaurants";
    public static final String RESTAURANTS_BY_COUNTY = "restaurantsByCounty";
    public static final String RESTAURANTS_CONTAINING_NAME = "restaurantsContainingName";
    public static final String RESTAURANTS_BY_NAME_AND_COUNTY = "restaurantsByNameAndCounty";
    public static final String RESTAURANT_BY_ID = "restaurantById";
    public static final String RESTAURANTS_WITH_CRITICAL_VIOLATIONS = "restaurantsWithCriticalViolations";
    public static final String VIOLATION_BY_ID = "violationsById";
    public static final String VIOLATIONS_BY_CATEGORY = "violationsByCategory";
    public static final String VIOLATIONS_BY_RESTAURANT_ID = "violationsByRestaurant";
    public static final String ALL_VIOLATIONS = "allViolations";
    private CacheConstants() {
    }
}
