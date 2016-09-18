package com.janeullah.healthinspectionrecords.domain.builders;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedInspectionReport;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;

/**
 * Author: Jane Ullah
 * Date:  4/22/2017
 */
public final class FlattenedRestaurantBuilder {
    private Long id;
    private int score;
    private int criticalViolations;
    private int nonCriticalViolations;
    private String name;
    private String dateReported;
    private String address;
    private String county;
    private FlattenedInspectionReport inspectionReport;

    private FlattenedRestaurantBuilder() {
    }

    public static FlattenedRestaurantBuilder aFlattenedRestaurant() {
        return new FlattenedRestaurantBuilder();
    }

    public FlattenedRestaurantBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public FlattenedRestaurantBuilder score(int score) {
        this.score = score;
        return this;
    }

    public FlattenedRestaurantBuilder criticalViolations(int criticalViolations) {
        this.criticalViolations = criticalViolations;
        return this;
    }

    public FlattenedRestaurantBuilder nonCriticalViolations(int nonCriticalViolations) {
        this.nonCriticalViolations = nonCriticalViolations;
        return this;
    }

    public FlattenedRestaurantBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FlattenedRestaurantBuilder dateReported(String dateReported) {
        this.dateReported = dateReported;
        return this;
    }

    public FlattenedRestaurantBuilder address(String address) {
        this.address = address;
        return this;
    }

    public FlattenedRestaurantBuilder county(String county) {
        this.county = county;
        return this;
    }

    public FlattenedRestaurantBuilder inspectionReport(FlattenedInspectionReport inspectionReport){
        this.inspectionReport = inspectionReport;
        return this;
    }

    public FlattenedRestaurant build() {
        FlattenedRestaurant flattenedRestaurant = new FlattenedRestaurant();
        flattenedRestaurant.setId(id);
        flattenedRestaurant.setScore(score);
        flattenedRestaurant.setCriticalViolations(criticalViolations);
        flattenedRestaurant.setNonCriticalViolations(nonCriticalViolations);
        flattenedRestaurant.setName(name);
        flattenedRestaurant.setDateReported(dateReported);
        flattenedRestaurant.setAddress(address);
        flattenedRestaurant.setCounty(county);
        flattenedRestaurant.setInspectionReport(inspectionReport);
        return flattenedRestaurant;
    }
}
