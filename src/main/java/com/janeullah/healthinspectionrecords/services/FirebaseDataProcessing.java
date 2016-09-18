package com.janeullah.healthinspectionrecords.services;

import com.google.common.base.CharMatcher;
import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.domain.builders.FlattenedRestaurantBuilder;
import com.janeullah.healthinspectionrecords.domain.dtos.County;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedInspectionReport;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedViolation;
import com.janeullah.healthinspectionrecords.domain.entities.InspectionReport;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: Jane Ullah
 * Date:  4/22/2017
 */
@Component
public class FirebaseDataProcessing {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseDataProcessing.class);
    private RestaurantRepository restaurantRepository;

    @Inject
    public FirebaseDataProcessing(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    private String replaceInvalidCharsInKey(String key) {
        return CharMatcher.anyOf("/.#$[]").replaceFrom(key, StringUtils.EMPTY);
    }

    public Map<String, County> createAndRetrieveMapOfCounties(Map<String, List<Restaurant>> mapOfCountiesToRestaurants) {
        Map<String, County> countiesAndRestaurants = new HashMap<>();
        for (String county : WebPageConstants.COUNTY_LIST) {
            List<Restaurant> restaurantsInCounty = restaurantRepository.findByEstablishmentInfoCountyIgnoreCase(county);
            mapOfCountiesToRestaurants.put(county, restaurantsInCounty);
            Map<String, FlattenedRestaurant> mapOfRestaurantsInCounty = restaurantsInCounty
                    .stream()
                    .collect(Collectors.toMap(createUniqueKey, getTrimmedRestaurantData));
            County countyObj = new County(county,mapOfRestaurantsInCounty);
            countiesAndRestaurants.put(county, countyObj);
        }
        return countiesAndRestaurants;
    }

    /**
     * http://www.baeldung.com/guava-string-charmatcher
     */
    private Function<Restaurant, String> createUniqueKey = restaurant -> {
        String nameAndId = restaurant.getEstablishmentInfo().getName() + "-" + restaurant.getId();
        return replaceInvalidCharsInKey(nameAndId);
    };

    private Function<FlattenedRestaurant,String> createUniqueKeyFromFlattenedRestaurant = restaurant -> {
        String nameAndId = restaurant.getName() + "-" + restaurant.getId();
        return replaceInvalidCharsInKey(nameAndId);
    };

    private Function<FlattenedInspectionReport,String> createUniqueKeyFromFlattenedInspectionReport = flattenedInspectionReport -> {
        String nameAndId = flattenedInspectionReport.getName() + "-" + flattenedInspectionReport.getId();
        return replaceInvalidCharsInKey(nameAndId);
    };

    private Function<Restaurant, FlattenedRestaurant> getTrimmedRestaurantData = restaurant -> Objects.nonNull(restaurant)
            ? processRestaurant(restaurant)
            : null;

    private FlattenedRestaurant processRestaurant(Restaurant restaurant) {
        FlattenedRestaurant flattenedRestaurant = new FlattenedRestaurant();
        flattenedRestaurant.setName(restaurant.getEstablishmentInfo().getName());
        flattenedRestaurant.setAddress(restaurant.getEstablishmentInfo().getAddress());
        return flattenedRestaurant;
    }

    /**
     * http://www.oracle.com/technetwork/articles/java/architect-streams-pt2-2227132.html
     * Map list of restaurants to list of flattened restaurant POJO
     * @param mapOfCountiesToRestaurants Map of County to List of Retaurant entities for that county
     * @return Map of county to flattened restaurant
     */
    public Map<String,FlattenedRestaurant> flattenMapOfRestaurants(Map<String,List<Restaurant>> mapOfCountiesToRestaurants){
        return mapOfCountiesToRestaurants
                .values()
                .stream()
                .flatMap(List::stream)
                .map(mapRestaurantEntityToFlattenedRestaurant)
                .collect(Collectors.toMap(createUniqueKeyFromFlattenedRestaurant,Function.identity()));
    }

    /**
     * Convert Restaurant entity to a POJO
     */
    private Function<Restaurant, FlattenedRestaurant> mapRestaurantEntityToFlattenedRestaurant = restaurant -> {
        logger.info("Flattening restaurant id {}",restaurant.getId());
        Optional<InspectionReport> mostRecentInspectionReport = restaurant.getInspectionReports()
                .stream()
                .max(Comparator.comparing(InspectionReport::getDateReported));
        if (mostRecentInspectionReport.isPresent()) {
            InspectionReport inspectionReport = mostRecentInspectionReport.get();
            Map<Severity, Long> mapOfSeverityToViolations = inspectionReport.getViolations()
                    .stream()
                    .collect(Collectors.groupingBy(Violation::getSeverity, Collectors.counting()));
            return FlattenedRestaurantBuilder.aFlattenedRestaurant()
                    .id(restaurant.getId())
                    .score(inspectionReport.getScore())
                    .name(restaurant.getEstablishmentInfo().getName())
                    .address(restaurant.getEstablishmentInfo().getAddress())
                    .county(restaurant.getEstablishmentInfo().getCounty())
                    .criticalViolations(MapUtils.getInteger(mapOfSeverityToViolations,Severity.CRITICAL,0))
                    .nonCriticalViolations(MapUtils.getInteger(mapOfSeverityToViolations,Severity.NONCRITICAL,0))
                    .dateReported(inspectionReport.getDateReported().toString())
                    .inspectionReport(createFlattenedInspectionReport(inspectionReport))
                    .build();

        }
        return new FlattenedRestaurant();
    };

    private  FlattenedInspectionReport createFlattenedInspectionReport(InspectionReport inspectionReport) {
        FlattenedInspectionReport flattenedInspectionReport = new FlattenedInspectionReport();
        flattenedInspectionReport.setDateReported(inspectionReport.getDateReported().toString());
        flattenedInspectionReport.setId(inspectionReport.getRestaurant().getId());
        flattenedInspectionReport.setName(inspectionReport.getRestaurant().getEstablishmentInfo().getName());
        flattenedInspectionReport.setScore(inspectionReport.getScore());
        flattenedInspectionReport.setViolations(createFlattenedViolations(inspectionReport));
        return flattenedInspectionReport;
    }

    private List<FlattenedViolation> createFlattenedViolations(InspectionReport inspectionReport) {
        List<FlattenedViolation> flattenedViolations = new ArrayList<>();
        for(Violation violation : inspectionReport.getViolations()){
            FlattenedViolation flattenedViolation = new FlattenedViolation();
            flattenedViolation.setCategory(violation.getCategory());
            flattenedViolation.setInspectionType(inspectionReport.getInspectionType().toString());
            flattenedViolation.setSection(violation.getSection());
            flattenedViolation.setSeverity(violation.getSeverity().toString());
            flattenedViolation.setViolationId(violation.getId());
            flattenedViolation.setSummary(violation.getSummary());
            flattenedViolation.setNotes(violation.getNotes());
            flattenedViolations.add(flattenedViolation);
        }
        return flattenedViolations;
    }

    public Map<String,FlattenedInspectionReport> createAndRetrieveViolations(Map<String, FlattenedRestaurant> restaurantData) {
        return restaurantData.values()
                .stream()
                .map(FlattenedRestaurant::getInspectionReport)
                .collect(Collectors.toMap(createUniqueKeyFromFlattenedInspectionReport,Function.identity()));
    }
}
