package com.janeullah.healthinspectionrecords.util;

import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;

public class TestUtil {

    public static Violation getSingleViolation(long id, Severity sev, String notes) {
        Violation violation = new Violation();
        violation.setId(id);
        violation.setSeverity(sev);
        violation.setNotes(notes);
        return violation;
    }
}
