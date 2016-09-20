package com.janeullah.healthinspectionrecords.org.util;

import com.janeullah.healthinspectionrecords.org.model.Restaurant;

import java.util.List;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class DatabaseUtil {
    public static void persistData(String countyFile, List<Restaurant> restaurants){
        System.out.format("Web Page Processing completed for county: %s size: %d\n",countyFile,restaurants.size());
    }
}
