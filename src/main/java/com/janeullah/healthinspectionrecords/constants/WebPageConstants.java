package com.janeullah.healthinspectionrecords.constants;

import com.janeullah.healthinspectionrecords.constants.counties.NEGACounties;

import java.util.List;

/** Author: Jane Ullah Date: 9/17/2016 */
public class WebPageConstants {
  public static final String BASE_URL = "http://publichealthathens.com/healthscores/";
  public static final String PAGE_URL = "_county_restaurant_scores.html";
  public static final String URL = BASE_URL + "%s" + PAGE_URL;
  public static final List<String> COUNTY_LIST = NEGACounties.getAllNEGACounties();
  public static final int NUMBER_OF_THREADS = COUNTY_LIST.size() / 2;
  public static final String VIOLATION_CODE_PREFIX = "Violation of :";
  public static final String VIOLATION_CODE_SUFFIX = ".";

  private WebPageConstants() {}
}
