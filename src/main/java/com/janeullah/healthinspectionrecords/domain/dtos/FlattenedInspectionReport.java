package com.janeullah.healthinspectionrecords.domain.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;

import java.util.List;

/**
 * Author: Jane Ullah
 * Date:  4/26/2017
 */
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FlattenedInspectionReport {
    private String name;
    private Long id;
    private int score;
    private String dateReported;
    private List<FlattenedViolation> violations;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDateReported() {
        return dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }

    public List<FlattenedViolation> getViolations() {
        return violations;
    }

    public void setViolations(List<FlattenedViolation> violations) {
        this.violations = violations;
    }
}
