package com.janeullah.healthinspectionrecords.external.firebase;

import com.google.common.base.CharMatcher;
import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.domain.dtos.County;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedInspectionReport;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedViolation;
import com.janeullah.healthinspectionrecords.domain.entities.InspectionReport;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.util.StringUtilities;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Author: Jane Ullah Date: 4/22/2017 */
@Slf4j
@Component
public class FirebaseDataProcessing {
  private RestaurantRepository restaurantRepository;
  /** http://www.baeldung.com/guava-string-charmatcher */
  private Function<Restaurant, String> createUniqueKey =
      restaurant -> {
        String nameAndId =
            restaurant.getEstablishmentInfo().getName()
                + StringUtilities.HYPHEN.getValue()
                + restaurant.getId();
        return replaceInvalidCharsInKey(nameAndId);
      };

  private Function<FlattenedRestaurant, String> createUniqueKeyFromFlattenedRestaurant =
      restaurant -> {
        String nameAndId =
            restaurant.getName() + StringUtilities.HYPHEN.getValue() + restaurant.getId();
        return replaceInvalidCharsInKey(nameAndId);
      };

  private Function<FlattenedInspectionReport, String> createUniqueKeyFromFlattenedInspectionReport =
      flattenedInspectionReport -> {
        String nameAndId =
            flattenedInspectionReport.getName()
                + StringUtilities.HYPHEN.getValue()
                + flattenedInspectionReport.getId();
        return replaceInvalidCharsInKey(nameAndId);
      };

  private Function<Restaurant, FlattenedRestaurant> getTrimmedRestaurantData =
      restaurant -> Objects.nonNull(restaurant) ? processRestaurant(restaurant) : null;

  /** Convert Restaurant entity to a POJO */
  private Function<Restaurant, FlattenedRestaurant> mapRestaurantEntityToFlattenedRestaurant =
      restaurant -> {
        log.info("Flattening restaurant id {}", restaurant.getId());
        Optional<InspectionReport> mostRecentInspectionReport =
            restaurant
                .getInspectionReports()
                .stream()
                .max(Comparator.comparing(InspectionReport::getDateReported));
        if (mostRecentInspectionReport.isPresent()) {
          InspectionReport inspectionReport = mostRecentInspectionReport.get();
          Map<Severity, Long> mapOfSeverityToViolations =
              inspectionReport
                  .getViolations()
                  .stream()
                  .collect(Collectors.groupingBy(Violation::getSeverity, Collectors.counting()));
          return FlattenedRestaurant.builder()
              .id(restaurant.getId())
              .score(inspectionReport.getScore())
              .name(restaurant.getEstablishmentInfo().getName())
              .address(restaurant.getEstablishmentInfo().getAddress())
              .county(restaurant.getEstablishmentInfo().getCounty())
              .criticalViolations(
                  mapOfSeverityToViolations.getOrDefault(Severity.CRITICAL, 0L).intValue())
              .nonCriticalViolations(
                  mapOfSeverityToViolations.getOrDefault(Severity.NONCRITICAL, 0L).intValue())
              .dateReported(inspectionReport.getDateReported().toString())
              .inspectionReport(createFlattenedInspectionReport(inspectionReport))
              .build();
        }
        return null;
      };

  @Autowired
  public FirebaseDataProcessing(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public static String replaceInvalidCharsInKey(String key) {
    return CharMatcher.anyOf(StringUtilities.FORBIDDEN_SEQUENCE.getValue())
        .replaceFrom(key, StringUtils.EMPTY);
  }

  Map<String, County> createAndRetrieveMapOfCounties(
      Map<String, List<Restaurant>> mapOfCountiesToRestaurants) {
    Map<String, County> countiesAndRestaurants = new HashMap<>();
    for (String county : WebPageConstants.COUNTY_LIST) {
      List<Restaurant> restaurantsInCounty =
          restaurantRepository.findByEstablishmentInfoCountyIgnoreCase(county);
      mapOfCountiesToRestaurants.put(county, restaurantsInCounty);
      Map<String, FlattenedRestaurant> mapOfRestaurantsInCounty =
          restaurantsInCounty
              .stream()
              .collect(Collectors.toMap(createUniqueKey, getTrimmedRestaurantData));
      countiesAndRestaurants.put(
          county, County.builder().name(county).restaurants(mapOfRestaurantsInCounty).build());
    }
    return countiesAndRestaurants;
  }

  private FlattenedRestaurant processRestaurant(Restaurant restaurant) {
    return FlattenedRestaurant.builder()
        .name(restaurant.getEstablishmentInfo().getName())
        .address(restaurant.getEstablishmentInfo().getAddress())
        .build();
  }

  /**
   * http://www.oracle.com/technetwork/articles/java/architect-streams-pt2-2227132.html Map list of
   * restaurants to list of flattened restaurant POJO
   *
   * @param mapOfCountiesToRestaurants Map of County to List of Retaurant entities for that county
   * @return Map of county to flattened restaurant
   */
  Map<String, FlattenedRestaurant> flattenMapOfRestaurants(
      Map<String, List<Restaurant>> mapOfCountiesToRestaurants) {
    return mapOfCountiesToRestaurants
        .values()
        .stream()
        .flatMap(List::stream)
        .map(mapRestaurantEntityToFlattenedRestaurant)
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(createUniqueKeyFromFlattenedRestaurant, Function.identity()));
  }

  private FlattenedInspectionReport createFlattenedInspectionReport(
      InspectionReport inspectionReport) {
    return FlattenedInspectionReport.builder()
        .dateReported(String.valueOf(inspectionReport.getDateReported()))
        .id(inspectionReport.getRestaurant().getId())
        .name(inspectionReport.getRestaurant().getEstablishmentInfo().getName())
        .score(inspectionReport.getScore())
        .violations(createFlattenedViolations(inspectionReport))
        .build();
  }

  private List<FlattenedViolation> createFlattenedViolations(InspectionReport inspectionReport) {
    List<FlattenedViolation> flattenedViolations = new ArrayList<>();
    for (Violation violation : inspectionReport.getViolations()) {
      FlattenedViolation flattenedViolation =
          FlattenedViolation.builder()
              .category(violation.getCategory())
              .inspectionType(String.valueOf(inspectionReport.getInspectionType()))
              .section(violation.getSection())
              .severity(String.valueOf(violation.getSeverity()))
              .violationId(violation.getId())
              .summary(violation.getSummary())
              .notes(violation.getNotes())
              .build();
      flattenedViolations.add(flattenedViolation);
    }
    return flattenedViolations;
  }

  Map<String, FlattenedInspectionReport> createAndRetrieveViolations(
      Map<String, FlattenedRestaurant> restaurantData) {
    return restaurantData
        .values()
        .stream()
        .map(FlattenedRestaurant::getInspectionReport)
        .collect(
            Collectors.toMap(createUniqueKeyFromFlattenedInspectionReport, Function.identity()));
  }
}
