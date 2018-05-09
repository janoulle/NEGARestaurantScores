package com.janeullah.healthinspectionrecords.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.janeullah.healthinspectionrecords.util.StringUtilities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import static com.janeullah.healthinspectionrecords.external.firebase.FirebaseDataProcessing.replaceInvalidCharsInKey;

@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FlattenedRestaurant {
  private Long id;
  private int score;
  private int criticalViolations;
  private int nonCriticalViolations;
  private String name;
  private String dateReported;
  private String address;
  private String county;

  @JsonIgnore
  private FlattenedInspectionReport inspectionReport;

  public String getNameKey() {
    String nameAndId = name + StringUtilities.HYPHEN.getValue() + id;
    return replaceInvalidCharsInKey(nameAndId);
  }

  //TODO: figure out how to cast directly via HQL in RestaurantRepository class
  //https://stackoverflow.com/questions/4791325/how-do-i-write-hql-query-with-cast
  public FlattenedRestaurant(
          Long id,
          int score,
          Integer criticalViolations,
          Integer nonCriticalViolations,
          String name,
          LocalDate lastDateReported,
          String address,
          String county) {
    this.id = id;
    this.score = score;
    this.criticalViolations = criticalViolations != null ? criticalViolations : 0;
    this.nonCriticalViolations = nonCriticalViolations != null ? nonCriticalViolations : 0;
    this.name = name;
    this.dateReported = lastDateReported == null ? "" : lastDateReported.toString();
    this.address = address;
    this.county = county;
  }

}
