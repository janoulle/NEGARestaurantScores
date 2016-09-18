package com.janeullah.healthinspectionrecords.org.model;

import com.janeullah.healthinspectionrecords.org.constants.InspectionType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

    public InspectionReport(int score, String date, InspectionType type){
        setScore(score);
        setDateReported(date);
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

    /**
     * http://stackoverflow.com/questions/20331163/how-to-format-joda-time-datetime-to-only-mm-dd-yyyy
     * @param date date inspection was conducted
     */
    private void setDateReported(String date){
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTime dateConverted = dtf.parseDateTime(date);
        setDateReported(dateConverted);
        //System.out.println(dtfOut.print(jodatime));
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
