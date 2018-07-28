package com.janeullah.healthinspectionrecords.constants;

/**
 * Author: Jane Ullah Date: 9/18/2016
 */
public class WebSelectorConstants {
    public static final String RESTAURANT_NAME_SELECTOR = ".estab_name";
    public static final String RESTAURANT_ADDRESS_SELECTOR = ".estab_addr";
    public static final String INSPECTION_TYPE_SELECTOR = ".type";
    public static final String DATE_SELECTOR = ".date";
    public static final String SCORE_SELECTOR = "tr td:first-child";
    public static final String ALL_ROW = "table.sorted > tbody > tr";
    public static final String CRITICAL = "div.crit a";
    public static final String NOT_CRITICAL = "div.ncrit a";
    public static final String HREF_PREFIX_FOR_VIOLATIONS = "crit_";

    private WebSelectorConstants() { }
}
