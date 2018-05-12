package com.janeullah.healthinspectionrecords.util;

import com.janeullah.healthinspectionrecords.constants.InspectionType;
import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.EstablishmentInfo;
import com.janeullah.healthinspectionrecords.domain.entities.InspectionReport;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import org.assertj.core.util.Lists;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class TestUtil {

  public static Violation getSingleViolation() {
    return getSingleViolation(null, Severity.CRITICAL, "Blah");
  }

  public static Violation getSingleViolation(Long id, Severity sev, String notes) {
    Violation violation = new Violation();
    violation.setId(id);
    violation.setSeverity(sev);
    violation.setNotes(notes);
    violation.setCategory("4-D");
    return violation;
  }

  //need to avoid PersistentObjectException: detached entity passed to persist
  public static InspectionReport getSingleInspectionReport() {
    InspectionReport inspectionReport = new InspectionReport();
    inspectionReport.setDateReported(LocalDate.parse("05/05/2018", DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    inspectionReport.setInspectionType(InspectionType.ROUTINE);
    inspectionReport.setScore(85);

    Violation v = getSingleViolation(null, Severity.CRITICAL, "Cook handled toast with bare hands. It was discarded.");
    v.setInspectionReport(inspectionReport);

    inspectionReport.addViolations(Collections.singletonList(v));
    return inspectionReport;
  }

  // https://stackoverflow.com/questions/6378526/org-hibernate-persistentobjectexception-detached-entity-passed-to-persist
  public static Restaurant getSingleRestaurant() {

    EstablishmentInfo establishmentInfo = new EstablishmentInfo();
    establishmentInfo.setCounty("Walton");
    establishmentInfo.setAddress("195 MLK JR. BLVD. MONROE GA, 30655");
    establishmentInfo.setName("ZAXBY'S-MONROE");

    Restaurant restaurant = new Restaurant();

    restaurant.setEstablishmentInfo(establishmentInfo);
    restaurant.addInspectionReport(getSingleInspectionReport());

    return restaurant;
  }

  // https://stackoverflow.com/questions/6378526/org-hibernate-persistentobjectexception-detached-entity-passed-to-persist
  public static List<Restaurant> getRestaurants() {

    EstablishmentInfo establishmentInfo = new EstablishmentInfo();
    establishmentInfo.setCounty("Walton");
    establishmentInfo.setAddress("195 MLK JR. BLVD. MONROE GA, 30655");
    establishmentInfo.setName("ZAXBY'S-MONROE");

    Restaurant restaurant = new Restaurant();

    restaurant.setEstablishmentInfo(establishmentInfo);
    restaurant.addInspectionReport(getSingleInspectionReport());


    EstablishmentInfo establishmentInfo2 = new EstablishmentInfo();
    establishmentInfo2.setCounty("Clark");
    establishmentInfo2.setAddress("400 CLAYTON STREET STE B ATHENS GA, 30601");
    establishmentInfo2.setName("9d's");

    Restaurant restaurant2 = new Restaurant();

    restaurant2.setEstablishmentInfo(establishmentInfo2);
    restaurant2.addInspectionReport(getSingleInspectionReport());

    return Lists.newArrayList(restaurant, restaurant2);
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
