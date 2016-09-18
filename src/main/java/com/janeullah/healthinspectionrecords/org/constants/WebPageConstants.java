package com.janeullah.healthinspectionrecords.org.constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageConstants {
    public final static String URL = System.getProperty("NEGA_BASE_URL","http://publichealthathens.com/healthscores/%s_county_restaurant_scores.html");
    public final static String DEFAULT_LIST_OF_COUNTIES = "Clarke,Elbert,Barrow,Madison,Jackson,Oconee,Greene,Walton,Oglethorpe,Morgan";
    public final static String COUNTIES = System.getProperty("NEGA_COUNTIES",DEFAULT_LIST_OF_COUNTIES);
    public final static List<String> COUNTY_LIST = Stream.of(WebPageConstants.COUNTIES.split(",")).collect(Collectors.toList());
    public final static int NUMBER_OF_THREADS = COUNTY_LIST.size();
    public final static String DATA_EXPIRATION_IN_DAYS = System.getProperty("EXPIRATION_IN_DAYS","1");
    public final static String PATH_TO_PAGE_STORAGE = "src/main/resources/downloads/webpages";
}
