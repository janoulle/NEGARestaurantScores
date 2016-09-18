package com.janeullah.healthinspectionrecords.org.util;

import org.jsoup.select.Elements;

import java.util.List;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class DatabaseUtil {
    public static void persistData(String county, List<Elements> elements){
        System.out.format("got here for county: %s size: %d\n",county,elements.size());
    }
    public static void persistData(List<Elements> elements){
        System.out.format("got here for size: %d\n",elements.size());
    }
}
