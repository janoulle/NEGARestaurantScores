package com.janeullah.healthinspectionrecords.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.janeullah.healthinspectionrecords.constants.Severity;

import javax.persistence.*;
import java.io.Serializable;

/** Author: Jane Ullah Date: 9/17/2016 */
@Entity
@Table(name = "ir_violations")
public class Violation implements Serializable {
  private static final long serialVersionUID = -6363640391316241510L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "severity")
  private Severity severity;

  @Column(name = "category")
  private String category;

  @Column(name = "section")
  private String section;

  @Column(name = "summary", columnDefinition = "varchar(10000)")
  private String summary;

  @Column(name = "notes", columnDefinition = "varchar(50000)")
  private String notes;

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(
    name = "inspection_report_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "FK_inspection_report_id")
  )
  private InspectionReport inspectionReport;

  public Violation() {}

  public Violation(String category, String section, String notes, Severity sev) {
    setCategory(category);
    setSection(section);
    setNotes(notes);
    setSeverity(sev);
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
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

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public InspectionReport getInspectionReport() {
    return inspectionReport;
  }

  public void setInspectionReport(InspectionReport inspectionReport) {
    this.inspectionReport = inspectionReport;
  }

  @Override
  public String toString() {
    return String.format(
        "Violation[severity=%s,category=%s,section=%s, summary=\"%s\", notes=\"%s\"]",
        severity, category, section, summary, notes);
  }
}
