package com.janeullah.healthinspectionrecords.domain.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;

/** Author: Jane Ullah Date: 4/26/2017 */
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FlattenedViolation {
  private Long violationId;
  private String severity;
  private String category;
  private String section;
  private String inspectionType;
  private String summary;
  private String notes;

  public Long getViolationId() {
    return violationId;
  }

  public void setViolationId(Long violationId) {
    this.violationId = violationId;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public String getInspectionType() {
    return inspectionType;
  }

  public void setInspectionType(String inspectionType) {
    this.inspectionType = inspectionType;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
