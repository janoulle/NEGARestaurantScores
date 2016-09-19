package com.janeullah.healthinspectionrecords.org.constants;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebSelectorConstants {
    public final static String RESTAURANT_NAME_SELECTOR = ".estab_name";
    public final static String RESTAURANT_ADDRESS_SELECTOR = ".estab_addr";
    public final static String INSPECTION_TYPE_SELECTOR = ".type";
    public final static String DATE_SELECTOR = ".date";
    public final static String SCORE_SELECTOR = "tr td:first-child";
    public final static String ALL_ROW = "table.sorted > tbody > tr";
    public final static String ALL_VIOLATIONS = "tr td:nth-child(6)";
    public final static String HIDDEN_DIVS = "div:hidden";
    public final static String CRITICAL = "div.crit a";
    public final static String NOT_CRITICAL = "div.ncrit a";
    public final static String HREF_PREFIX_FOR_VIOLATIONS = "crit_";
}
