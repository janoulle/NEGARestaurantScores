package com.janeullah.healthinspectionrecords.org.constants;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public enum InspectionType {
    PRELIMINARY("Preliminary"),
    ROUTINE("Routine"),
    RE_INSPECTION("Routine / Follow-Up");

    private final String value;

    InspectionType(String type){
        this.value = type;
    }

    String getValue(){
        return value;
    }

    public static InspectionType asInspectionType(String str) {
        for (InspectionType me : InspectionType.values()) {
            if (me.getValue().equalsIgnoreCase(str))
                return me;
        }
        return null;
    }
}
