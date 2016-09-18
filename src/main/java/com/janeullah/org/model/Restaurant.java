package com.janeullah.org.model;

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
    private String address;

    public Restaurant(){

    }

    public Restaurant(String address){
        setAddress(address);
    }

    public List<InspectionReport> getInspectionReports() {
        return inspectionReports;
    }

    public void setInspectionReports(List<InspectionReport> inspectionReports) {
        this.inspectionReports = inspectionReports;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
