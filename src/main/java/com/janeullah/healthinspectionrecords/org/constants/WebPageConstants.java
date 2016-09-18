package com.janeullah.healthinspectionrecords.org.constants;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageConstants {
    public final static String URL = System.getProperty("NEGA_BASE_URL","http://publichealthathens.com/healthscores/%s_county_restaurant_scores.html");
    public final static String DEFAULT_LIST_OF_COUNTIES = "Clarke,Elbert,Barrow,Madison,Jackson,Oconee,Greene,Walton,Oglethorpe,Morgan";
    public final static String COUNTIES = System.getProperty("NEGA_COUNTIES",DEFAULT_LIST_OF_COUNTIES);
}
