package com.janeullah.healthinspectionrecords.constants;

import com.janeullah.healthinspectionrecords.constants.counties.NEGACounties;

import java.util.List;

/**
 * Author: Jane Ullah
 * Date:  9/17/2016
 */
public class WebPageConstants {
    public final static String BASE_URL = System.getProperty("NEGA_BASE_URL","http://publichealthathens.com/healthscores/");
    public final static String PAGE_URL = System.getProperty("NEGA_PAGE_URL","_county_restaurant_scores.html");
    public final static String URL = System.getProperty("NEGA_FULL_URL",BASE_URL + "%s" + PAGE_URL);
    public final static List<String> COUNTY_LIST = NEGACounties.getAllNEGACounties();
    public final static int NUMBER_OF_THREADS = COUNTY_LIST.size() / 2;
    public final static String VIOLATION_CODE_PREFIX = "Violation of :";
    public final static String VIOLATION_CODE_SUFFIX = ".";

    public final static String DATA_EXPIRATION_IN_DAYS = System.getProperty("EXPIRATION_IN_DAYS","7");
    public final static boolean DOWNLOAD_OVERRIDE = Boolean.parseBoolean(System.getProperty("DOWNLOAD_OVERRIDE","false"));
    public final static boolean SET_WATCHER = Boolean.parseBoolean(System.getProperty("SET_WATCHER","false"));

    public final static String CSV_PATH_STORAGE = System.getProperty("CSV_PATH_STORAGE","src/main/resources/downloads/uga/soc");
    public final static String PATH_TO_PAGE_STORAGE = System.getProperty("PATH_TO_PAGE_STORAGE","src/main/resources/downloads/webpages");
    public final static String WATCHABLE_EVENTS = System.getProperty("WATCHABLE_EVENT","ENTRY_CREATE,ENTRY_MODIFY,ENTRY_DELETE");
    public final static String USER_AGENT = System.getProperty("USER_AGENT","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
}
