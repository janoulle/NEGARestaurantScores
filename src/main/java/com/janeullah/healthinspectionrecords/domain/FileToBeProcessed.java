package com.janeullah.healthinspectionrecords.domain;

import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

public class FileToBeProcessed {
    private Path file;
    private String countyName;

    public FileToBeProcessed(Path file) {
        this.file = file;
        setCountyName();
    }

    private void setCountyName() {
        String fileName = file.getFileName().toString();
        countyName =
                StringUtils.isNotBlank(fileName) && fileName.length() > WebPageConstants.PAGE_URL.length()
                        ? fileName.substring(0, fileName.indexOf(WebPageConstants.PAGE_URL))
                        : "";
    }

    public String getCountyName() {
        return countyName;
    }
}