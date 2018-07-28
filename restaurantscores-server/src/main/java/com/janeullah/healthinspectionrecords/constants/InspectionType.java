package com.janeullah.healthinspectionrecords.constants;

import java.util.Arrays;
import java.util.Optional;

/**
 * Author: Jane Ullah Date: 9/17/2016
 */
public enum InspectionType {
    PRELIMINARY("Preliminary"),
    ROUTINE("Routine"),
    RE_INSPECTION("Routine / Follow-Up");

    private final String value;

    InspectionType(String type) {
        this.value = type;
    }

    public static Optional<InspectionType> asInspectionType(String str) {
        return Arrays.stream(InspectionType.values())
                .filter(me -> me.getValue().equalsIgnoreCase(str))
                .findFirst();
    }

    String getValue() {
        return value;
    }
}
