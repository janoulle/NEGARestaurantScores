package com.janeullah.healthinspectionrecords.util;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;

public class TestFileUtil {

    public static final File[] FILES = TestFileUtil.getFilesInDirectory("./src/test/resources/downloads/webpages");

    public static File[] getFilesInDirectory(String path) {
        try {
            File dir = Paths.get(path).toFile();
            return dir.listFiles();
        } catch (InvalidPathException | SecurityException e) {
            System.err.println("Unable to retrieve list of files" + Arrays.toString(e.getStackTrace()));
        }
        return new File[0];
    }
}
