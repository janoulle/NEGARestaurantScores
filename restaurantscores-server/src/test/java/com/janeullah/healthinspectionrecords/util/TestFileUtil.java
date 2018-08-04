package com.janeullah.healthinspectionrecords.util;

import feign.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;

public class TestFileUtil {

    public static final File[] FILES = getFilesInDirectory("./src/test/resources/downloads/webpages");

    public static final Response.Body INDEX_NOT_EXISTING = new Response.Body() {
        @Override
        public void close() throws IOException {

        }

        @Override
        public Integer length() {
            return null;
        }

        @Override
        public boolean isRepeatable() {
            return false;
        }

        @Override
        public InputStream asInputStream() throws IOException {
            return TestFileUtil.readFile("src/test/resources/heroku/restaurants-index-not-existing.json");
        }

        @Override
        public Reader asReader() throws IOException {
            return null;
        }
    };

    public static final Response.Body INDEX_ALREADY_EXISTS = new Response.Body() {
        @Override
        public void close() throws IOException {

        }

        @Override
        public Integer length() {
            return null;
        }

        @Override
        public boolean isRepeatable() {
            return false;
        }

        @Override
        public InputStream asInputStream() throws IOException {
            return TestFileUtil.readFile("src/test/resources/heroku/restaurants-index-already-exists.json");
        }

        @Override
        public Reader asReader() throws IOException {
            return null;
        }
    };

    public static File[] getFilesInDirectory(String path) {
        try {
            File dir = Paths.get(path).toFile();
            return dir.listFiles();
        } catch (InvalidPathException | SecurityException e) {
            System.err.println("Unable to retrieve list of files" + Arrays.toString(e.getStackTrace()));
        }
        return new File[0];
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static InputStream readFile(String path) throws IOException {
        return Files.newInputStream(Paths.get(path));
    }
}
