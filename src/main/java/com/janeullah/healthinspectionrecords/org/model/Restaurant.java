package com.janeullah.healthinspectionrecords.org.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class Restaurant implements Serializable {

    private static final long serialVersionUID = -7700847544993366495L;
    private List<InspectionReport> inspectionReports = new ArrayList<>();
    private EstablishmentInfo establishmentInfo;

    public Restaurant(){

    }

    public List<InspectionReport> getInspectionReports() {
        return inspectionReports;
    }

    public void setInspectionReports(List<InspectionReport> inspectionReports) {
        this.inspectionReports = inspectionReports;
    }

    public void addInspectionReport(InspectionReport report){
        this.inspectionReports.add(report);
    }

    public EstablishmentInfo getEstablishmentInfo() {
        return establishmentInfo;
    }

    public void setEstablishmentInfo(EstablishmentInfo establishmentInfo) {
        this.establishmentInfo = establishmentInfo;
    }

}
