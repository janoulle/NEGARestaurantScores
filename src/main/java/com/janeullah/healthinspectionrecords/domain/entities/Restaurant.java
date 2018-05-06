package com.janeullah.healthinspectionrecords.domain.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Author: Jane Ullah Date: 9/17/2016 */
@Data
@Entity
@Table(name = "ir_restaurants")
public class Restaurant implements Serializable {

  private static final long serialVersionUID = -7700847544993366495L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToMany(
    mappedBy = "restaurant",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true
  )
  private List<InspectionReport> inspectionReports = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(
    name = "establishment_info_id",
    foreignKey = @ForeignKey(name = "FK_establishment_info_id")
  )
  private EstablishmentInfo establishmentInfo;

  //This was done to establish relationships during the data generation step before persisting to the db
  public void addInspectionReport(InspectionReport report) {
    this.inspectionReports.add(report);
    report.setRestaurant(this);
  }

  public void setEstablishmentInfo(EstablishmentInfo establishmentInfo) {
    this.establishmentInfo = establishmentInfo;
    establishmentInfo.setRestaurant(this);
  }
}
