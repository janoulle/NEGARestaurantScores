package com.janeullah.healthinspectionrecords.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.janeullah.healthinspectionrecords.constants.Severity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/** Author: Jane Ullah Date: 9/17/2016 */
@Data
@ToString(exclude="inspectionReport")
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

}
