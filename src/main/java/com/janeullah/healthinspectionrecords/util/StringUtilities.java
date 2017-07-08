package com.janeullah.healthinspectionrecords.util;

/**
 * Author: jane
 * Date:  7/7/2017
 */
public enum StringUtilities {
    COMMA(","),
    DOT("."),
    COLON(":"),
    SEMICOLON(";"),
    APOSTROPHE("'"),
    FORBIDDEN_SEQUENCE("/.#$[]"),
    HYPHEN("-"),
    UNDERSCORE("_");

    String value;

    StringUtilities(String val){
        this.value = val;
    }

    public String getValue(){
        return value;
    }
}
