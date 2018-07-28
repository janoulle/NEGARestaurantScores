package com.janeullah.healthinspectionrecords.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jane Ullah Date: 9/17/2016
 */
@Data
@ToString(exclude = {"criticalCount", "nonCriticalCount"})
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

    @JsonIgnore
    @Formula(
            "(SELECT COUNT(ir_violations.id)\n"
                    + "  FROM ir_restaurants INNER JOIN \n"
                    + "  ir_inspectionreport ON ir_restaurants.id = ir_inspectionreport.restaurant_id  INNER JOIN\n"
                    + "  ir_violations ON ir_inspectionreport.id = ir_violations.inspection_report_id \n"
                    + "  WHERE ir_violations.severity = 3 and ir_inspectionreport.restaurant_id = id\n"
                    + "  GROUP BY ir_violations.severity)")
    private Integer criticalCount;

    /*
    Notes:
    1. setting to Integer since an empty result set translates to null which an int can't handle
    2. PostLoad/Transient doesn't work for use in a repository
    3. Ignoring this from output because the lists of violations are already emitted
    */
    @JsonIgnore
    @Formula(
            "(SELECT COUNT(ir_violations.id)\n"
                    + "  FROM ir_restaurants INNER JOIN \n"
                    + "  ir_inspectionreport ON ir_restaurants.id = ir_inspectionreport.restaurant_id  INNER JOIN\n"
                    + "  ir_violations ON ir_inspectionreport.id = ir_violations.inspection_report_id \n"
                    + "  WHERE ir_violations.severity = 2 and ir_inspectionreport.restaurant_id = id\n"
                    + "  GROUP BY ir_violations.severity)")
    private Integer nonCriticalCount;

    // This was done to establish relationships during the data generation step before persisting to
    // the db
    public void addInspectionReport(InspectionReport report) {
        this.inspectionReports.add(report);
        report.setRestaurant(this);
    }

    public void setEstablishmentInfo(EstablishmentInfo establishmentInfo) {
        this.establishmentInfo = establishmentInfo;
        establishmentInfo.setRestaurant(this);
    }
}
