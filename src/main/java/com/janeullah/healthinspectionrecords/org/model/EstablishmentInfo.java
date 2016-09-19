package com.janeullah.healthinspectionrecords.org.model;

import java.io.Serializable;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class EstablishmentInfo implements Serializable{
    private String address;
    private String name;
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
