package com.janeullah.healthinspectionrecords.constants;

/**
 * Author: Jane Ullah
 * Date:  9/22/2016
 */
public class DatabaseConstants {
    public static final String ID_COL_NAME = getIdColName();

    private DatabaseConstants(){}

    public static String getIdColName(){
        String profile = System.getenv("spring.profiles.active");
        if ("sqlite".equalsIgnoreCase(profile)){
            return "_id";
        }
        return "id";
    }
}
