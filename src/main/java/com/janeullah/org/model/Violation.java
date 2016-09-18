package com.janeullah.org.model;

import com.janeullah.org.util.constants.Severity;

import java.io.Serializable;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class Violation  implements Serializable {
    private static final long serialVersionUID = -6363640391316241510L;
    private Severity severity;
    private String code;
    private String section;
    private String notes;

    public Violation(){

    }

    public Violation(String code, String section, String notes, Severity sev){
        setCode(code);
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

}
