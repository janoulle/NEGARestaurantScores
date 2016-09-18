package com.janeullah.healthinspectionrecords.constants;

/**
 * Author: Jane Ullah
 * Date:  3/28/2017
 */
public enum UgaCsvColumnNameEnum {
    SUBJECT("SUBJECT"),
    CRN_SEC("CRN SEC"),
    COURSE_NO("COURSE NO"),
    TITLE("TITLE"),
    STAT_CREDIT_HRS("STAT CREDIT HRS"),
    TIME("TIME"),
    DEPARTMENT("DEPARTMENT"),
    BLDG_ROOM("BLDG ROOM"),
    CAMPUS("CAMPUS"),
    INSTRUCTOR("INSTRUCTOR"),
    PART_OF("PART OF"),
    TERM("TERM"),
    CLS("CLS"),
    SIZE("SIZE"),
    SEATS("SEATS"),
    AVL("AVL");

    String columnName;

    UgaCsvColumnNameEnum(String name){
        this.columnName = name;
    }

    String getColumnName(){
        return columnName;
    }
}
