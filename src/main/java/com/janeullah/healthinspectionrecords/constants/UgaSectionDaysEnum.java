package com.janeullah.healthinspectionrecords.constants;

/**
 * Author: Jane Ullah
 * Date:  3/28/2017
 */
public enum UgaSectionDaysEnum {
    MONDAY("M"),
    TUESDAY("T"),
    WEDNESDAY("W"),
    THURSDAY("R"),
    FRIDAY("F");

    String shortName;

    UgaSectionDaysEnum(String shortName){
        this.shortName = shortName;
    }

    String getShortName(){
        return shortName;
    }
}
