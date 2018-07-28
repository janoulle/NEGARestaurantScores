package com.janeullah.healthinspectionrecords.domain;

import com.google.common.base.Preconditions;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileToBeProcessed {
    // Barrow_county_restaurant_scores.html should match just 'Barrow'
    private static final Pattern MATCH_TILL_FIRST_UNDERSCORE = Pattern.compile("^[^_]+(?=_)");
    private Path file;
    private String countyName;

    public FileToBeProcessed(Path file) {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(file.getFileName());
        this.file = file;
        setCountyName();
    }

    private void setCountyName() {
        String fileName = file.getFileName().toString();
        Matcher matcher = MATCH_TILL_FIRST_UNDERSCORE.matcher(fileName);
        countyName = matcher.lookingAt() ? matcher.group() : "";
    }

    public String getCountyName() {
        return countyName;
    }

    public Path getFile() {
        return file;
    }
}