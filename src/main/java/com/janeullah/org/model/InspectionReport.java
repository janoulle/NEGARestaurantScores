package com.janeullah.org.model;

import com.janeullah.org.util.constants.InspectionType;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class InspectionReport implements Serializable{
    private static final long serialVersionUID = 7350274689519197082L;
    private DateTime dateReported;
    private InspectionType inspectionType;
    private List<Violation> violations = new ArrayList<>();
    private int score;

    public InspectionReport(){

    }

    public InspectionReport(int score, DateTime time, InspectionType type){
        setScore(score);
        setDateReported(time);
        setInspectionType(type);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public DateTime getDateReported() {
        return dateReported;
    }

    public void setDateReported(DateTime dateReported) {
        this.dateReported = dateReported;
    }

    public InspectionType getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(InspectionType inspectionType) {
        this.inspectionType = inspectionType;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

}
