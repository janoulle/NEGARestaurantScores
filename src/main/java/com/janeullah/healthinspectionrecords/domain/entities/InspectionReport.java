package com.janeullah.healthinspectionrecords.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.janeullah.healthinspectionrecords.constants.InspectionType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Author: Jane Ullah Date: 9/17/2016 */
@Data
@ToString(exclude = {"restaurant"})
@Entity
@Table(name = "ir_inspectionreport")
public class InspectionReport implements Serializable {
  public static final DateTimeFormatter MMddYYYY_PATTERN =
      DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final long serialVersionUID = 7350274689519197082L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "date_reported")
  private LocalDate dateReported;

  @Column(name = "inspection_type")
  private InspectionType inspectionType;

  @OneToMany(
    mappedBy = "inspectionReport",
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER,
    orphanRemoval = true
  )
  private List<Violation> violations = new ArrayList<>();

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(
    name = "restaurant_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "FK_restaurant_id")
  )
  private Restaurant restaurant;

  @Column(name = "score")
  private int score;

  /**
   * http://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/
   * https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
   * http://stackoverflow.com/questions/20331163/how-to-format-joda-time-datetime-to-only-mm-dd-yyyy
   * Used by a converter
   * @param date date inspection was conducted
   */
  @SuppressWarnings("Unused")
  private void setDateReported(String date) {
    setDateReported(LocalDate.parse(date, MMddYYYY_PATTERN));
  }

  public void setDateReported(LocalDate dateReported) {
    this.dateReported = dateReported;
  }

  public void addViolations(List<Violation> violations) {
    setViolations(violations);
    violations.forEach(violation -> violation.setInspectionReport(this));
  }

  // @Transient - https://stackoverflow.com/questions/20597930/using-a-transient-field-in-hql
  //  @PostLoad
  //  public void getCriticalViolationsCount() {
  //    Map<Severity, Long> mapOfSeverityToViolations = violations
  //                    .stream()
  //                    .collect(Collectors.groupingBy(Violation::getSeverity,
  // Collectors.counting()));
  //    criticalCount =  mapOfSeverityToViolations.get(Severity.CRITICAL).intValue();
  //    nonCriticalCount =  mapOfSeverityToViolations.get(Severity.NONCRITICAL).intValue();
  //  }

}
