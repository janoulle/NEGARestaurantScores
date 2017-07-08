package com.janeullah.healthinspectionrecords.constants;

import com.janeullah.healthinspectionrecords.constants.counties.NEGACounties;

import java.util.List;

/**
 * Author: Jane Ullah
 * Date:  9/17/2016
 */
public class WebPageConstants {
    public static final String BASE_URL = System.getProperty("NEGA_BASE_URL","http://publichealthathens.com/healthscores/");
    public static final String PAGE_URL = System.getProperty("NEGA_PAGE_URL","_county_restaurant_scores.html");
    public static final String URL = System.getProperty("NEGA_FULL_URL",BASE_URL + "%s" + PAGE_URL);
    public static final List<String> COUNTY_LIST = NEGACounties.getAllNEGACounties();
    public static final int NUMBER_OF_THREADS = COUNTY_LIST.size() / 2;
    public static final String VIOLATION_CODE_PREFIX = "Violation of :";
    public static final String VIOLATION_CODE_SUFFIX = ".";

    public static final String DATA_EXPIRATION_IN_DAYS = System.getProperty("EXPIRATION_IN_DAYS","7");
    public static final boolean DOWNLOAD_OVERRIDE = Boolean.parseBoolean(System.getProperty("DOWNLOAD_OVERRIDE","false"));
    public static final boolean SET_WATCHER = Boolean.parseBoolean(System.getProperty("SET_WATCHER","false"));

    public static final String CSV_PATH_STORAGE = System.getProperty("CSV_PATH_STORAGE","src/main/resources/downloads/uga/soc");
    public static final String PATH_TO_PAGE_STORAGE = System.getProperty("PATH_TO_PAGE_STORAGE","src/main/resources/downloads/webpages");
    public static final String WATCHABLE_EVENTS = System.getProperty("WATCHABLE_EVENT","ENTRY_CREATE,ENTRY_MODIFY,ENTRY_DELETE");
    public static final String USER_AGENT = System.getProperty("USER_AGENT","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

    private WebPageConstants(){}
}
