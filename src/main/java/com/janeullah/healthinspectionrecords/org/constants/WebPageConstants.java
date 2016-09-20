package com.janeullah.healthinspectionrecords.org.constants;

import com.google.common.primitives.Ints;
import org.joda.time.DateTimeConstants;

import java.nio.file.WatchEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageConstants {
    public final static String BASE_URL = System.getProperty("NEGA_BASE_URL","http://publichealthathens.com/healthscores/");
    public final static String PAGE_URL = System.getProperty("NEGA_PAGE_URL","_county_restaurant_scores.html");
    public final static String URL = System.getProperty("NEGA_FULL_URL",BASE_URL + "%s" + PAGE_URL);
    public final static String DEFAULT_LIST_OF_COUNTIES = "Clarke,Elbert,Barrow,Madison,Jackson,Oconee,Greene,Walton,Oglethorpe,Morgan";
    public final static String COUNTIES = System.getProperty("NEGA_COUNTIES",DEFAULT_LIST_OF_COUNTIES);
    public final static List<String> COUNTY_LIST = Stream.of(WebPageConstants.COUNTIES.split(",")).collect(Collectors.toList());
    public final static int NUMBER_OF_THREADS = COUNTY_LIST.size();
    public final static String VIOLATION_CODE_PREFIX = "Violation of :";
    public final static String VIOLATION_CODE_SUFFIX = ".";


    public final static String WATCHABLE_EVENTS = System.getProperty("WATCHABLE_EVENT","ENTRY_CREATE,ENTRY_MODIFY,ENTRY_DELETE");

    public final static String DATA_EXPIRATION_IN_DAYS = System.getProperty("EXPIRATION_IN_DAYS","1");
    public final static AtomicLong DATA_EXPIRATION_IN_MILLIS = new AtomicLong(Ints.tryParse(DATA_EXPIRATION_IN_DAYS) * DateTimeConstants.MILLIS_PER_DAY);
    public final static String OLD_PATH_TO_PAGE_STORAGE = System.getProperty("PATH_TO_PAGE_STORAGE","downloads/webpages");
    public final static String PATH_TO_PAGE_STORAGE = System.getProperty("PATH_TO_PAGE_STORAGE","src/main/resources/downloads/webpages");
    public final static boolean DOWNLOAD_OVERRIDE = Boolean.parseBoolean(System.getProperty("DOWNLOAD_OVERRIDE","false"));
    public static boolean SET_WATCHER = Boolean.parseBoolean(System.getProperty("SET_WATCHER","false"));

}
