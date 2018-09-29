package com.janeullah.healthinspectionrecords.constants;

public enum HttpHeaders {
    HA_POLICY("x-ha-policy");

    private String header;

    HttpHeaders(String header) {
        this.header = header;
    }

    public String getName() {
        return header;
    }
}
