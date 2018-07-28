package com.janeullah.healthinspectionrecords.constants;

/**
 * Author: Jane Ullah Date: 9/17/2016
 */
public class WebPageConstants {
    public static final String BASE_URL = "http://publichealthathens.com/healthscores/";
    public static final String PAGE_URL = "_county_restaurant_scores.html";
    public static final String URL = BASE_URL + "%s" + PAGE_URL;
    public static final String VIOLATION_CODE_PREFIX = "Violation of :";
    public static final String VIOLATION_CODE_SUFFIX = ".";

    private WebPageConstants() {
    }
}
