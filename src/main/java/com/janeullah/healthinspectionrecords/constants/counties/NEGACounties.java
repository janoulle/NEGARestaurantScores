package com.janeullah.healthinspectionrecords.constants.counties;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: Jane Ullah
 * Date:  3/26/2017
 */
public enum NEGACounties {
    CLARKE,
    ELBERT,
    BARROW,
    MADISON,
    JACKSON,
    OCONEE,
    GREENE,
    WALTON,
    OGLETHORPE,
    MORGAN;

    public static List<String> getAllNEGACounties(){
        return Stream.of(NEGACounties.values())
                .map(enumVal -> WordUtils.capitalize(enumVal.toString().toLowerCase()))
                .collect(Collectors.toList());
    }
}
