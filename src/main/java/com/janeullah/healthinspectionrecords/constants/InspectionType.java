package com.janeullah.healthinspectionrecords.constants;

import java.util.Arrays;

/**
 * Author: Jane Ullah
 * Date:  9/17/2016
 */
public enum InspectionType {
    PRELIMINARY("Preliminary"),
    ROUTINE("Routine"),
    RE_INSPECTION("Routine / Follow-Up");

    private final String value;

    InspectionType(String type) {
        this.value = type;
    }

    public static InspectionType asInspectionType(String str) {
        return Arrays.stream(InspectionType.values())
                .filter(me -> me.getValue().equalsIgnoreCase(str))
                .findFirst().orElse(null);
    }

    String getValue() {
        return value;
    }
}
