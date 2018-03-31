package com.janeullah.healthinspectionrecords.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.janeullah.healthinspectionrecords.constants.InspectionType;
import com.janeullah.healthinspectionrecords.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Author: Jane Ullah Date: 9/17/2016 */
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

  public InspectionReport() {}

  public InspectionReport(int score, String date, InspectionType type) {
    setScore(score);
    setDateReported(date);
    setInspectionType(type);
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public LocalDate getDateReported() {
    return dateReported;
  }

  public void setDateReported(LocalDate dateReported) {
    this.dateReported = dateReported;
  }

  /**
   * http://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/
   * https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
   * http://stackoverflow.com/questions/20331163/how-to-format-joda-time-datetime-to-only-mm-dd-yyyy
   *
   * @param date date inspection was conducted
   */
  private void setDateReported(String date) {
    setDateReported(LocalDate.parse(date, MMddYYYY_PATTERN));
  }

  public InspectionType getInspectionType() {
    return inspectionType;
  }

  public void setInspectionType(InspectionType inspectionType) {
    this.inspectionType = inspectionType;
  }

  public List<Violation> getViolations() {
    return violations;
  }

  public void setViolations(List<Violation> violations) {
    this.violations = violations;
  }

  public void addViolation(Violation violation) {
    violations.add(violation);
    violation.setInspectionReport(this);
  }

  public void addViolations(List<Violation> violations) {
    setViolations(violations);
    violations.forEach(violation -> violation.setInspectionReport(this));
  }

  public void removeViolation(Violation violation) {
    violations.remove(violation);
    violation.setInspectionReport(null);
  }

  public Restaurant getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(Restaurant restaurant) {
    this.restaurant = restaurant;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  private String getViolationsAsString() {
    if (CollectionUtils.isEmpty(violations)) {
      return StringUtils.EMPTY;
    }
    return violations
        .stream()
        .map(Violation::toString)
        .collect(Collectors.joining(StringUtilities.COMMA.getValue()));
  }

  @Override
  public String toString() {
    return String.format(
        "InspectionReport[score=%s,dateReported=%s,violations=%s, restaurant=\"%s\"]",
        score, dateReported, getViolationsAsString(), restaurant.toString());
  }
}
