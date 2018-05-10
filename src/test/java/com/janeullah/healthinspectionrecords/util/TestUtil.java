package com.janeullah.healthinspectionrecords.util;

import com.janeullah.healthinspectionrecords.constants.InspectionType;
import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.EstablishmentInfo;
import com.janeullah.healthinspectionrecords.domain.entities.InspectionReport;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class TestUtil {

  public static Violation getSingleViolation(long id, Severity sev, String notes) {
    Violation violation = new Violation();
    violation.setId(id);
    violation.setSeverity(sev);
    violation.setNotes(notes);
    return violation;
  }

  public static Restaurant getSingleRestaurant() {
    EstablishmentInfo establishmentInfo = new EstablishmentInfo();
    establishmentInfo.setId(4L);
    establishmentInfo.setCounty("Walton");
    establishmentInfo.setAddress("195 MLK JR. BLVD. MONROE GA, 30655");
    establishmentInfo.setName("ZAXBY'S-MONROE");

    InspectionReport inspectionReport = new InspectionReport();
    inspectionReport.setDateReported(LocalDate.parse("05/05/2018", DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    inspectionReport.setInspectionType(InspectionType.ROUTINE);
    inspectionReport.setScore(85);
    inspectionReport.setViolations(Collections.singletonList(getSingleViolation(1, Severity.CRITICAL, "Cook handled toast with bare hands. It was discarded.")));

    Restaurant restaurant = new Restaurant();
    restaurant.setId(1L);
    restaurant.setEstablishmentInfo(establishmentInfo);
    restaurant.setInspectionReports(Collections.singletonList(inspectionReport));

    return restaurant;
  }

  public static FlattenedRestaurant getSingleFlattenedRestaurant() {
    return FlattenedRestaurant.builder()
            .address("195 MLK JR. BLVD. MONROE GA, 30655")
            .name("ZAXBY'S-MONROE")
            .county("WALTON")
            .criticalViolations(2)
            .nonCriticalViolations(5)
            .score(84)
            .dateReported("2018-01-29")
            .id(1L)
            .build();
  }
}
