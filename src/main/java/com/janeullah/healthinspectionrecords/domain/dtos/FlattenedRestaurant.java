package com.janeullah.healthinspectionrecords.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.janeullah.healthinspectionrecords.util.StringUtilities;
import lombok.ToString;

import java.time.LocalDate;

import static com.janeullah.healthinspectionrecords.external.firebase.FirebaseDataProcessing.replaceInvalidCharsInKey;

/**
 * Author: Jane Ullah
 * Date:  4/22/2017
 */
@ToString
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

    public FlattenedRestaurant() {
        /*
        Default constructor
         */
    }

    //added for use in repository
    public FlattenedRestaurant(Long id, int score, int criticalViolations, int nonCriticalViolations, LocalDate lastDateReported, String name, String address, String county) {
        this(id, score, criticalViolations, nonCriticalViolations, lastDateReported.toString(), name, address, county);
    }

    public FlattenedRestaurant(Long id, int score, int criticalViolations, int nonCriticalViolations, String lastDateReported, String name, String address, String county) {
        this.id = id;
        this.score = score;
        this.criticalViolations = criticalViolations;
        this.nonCriticalViolations = nonCriticalViolations;
        this.name = name;
        this.dateReported = lastDateReported;
        this.address = address;
        this.county = county;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCriticalViolations() {
        return criticalViolations;
    }

    public void setCriticalViolations(int criticalViolations) {
        this.criticalViolations = criticalViolations;
    }

    public int getNonCriticalViolations() {
        return nonCriticalViolations;
    }

    public void setNonCriticalViolations(int nonCriticalViolations) {
        this.nonCriticalViolations = nonCriticalViolations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateReported() {
        return dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public FlattenedInspectionReport getInspectionReport() {
        return inspectionReport;
    }

    public void setInspectionReport(FlattenedInspectionReport flattenedInspectionReport) {
        this.inspectionReport = flattenedInspectionReport;
    }

    public String getNameKey() {
        String nameAndId = name + StringUtilities.HYPHEN.getValue() + id;
        return replaceInvalidCharsInKey(nameAndId);
    }
}
