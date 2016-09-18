package com.janeullah.healthinspectionrecords.util;


import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * http://www.vogella.com/tutorials/JavaIO/article.html#javaio_writefile
 * Author: Jane Ullah
 * Date:  9/17/2016
 */
public class FilesUtil {
    private final static Logger logger = LoggerFactory.getLogger(FilesUtil.class);

    private static void touchFiles(){
        try {
            com.google.common.io.Files.touch(Paths.get(WebPageConstants.PATH_TO_PAGE_STORAGE).toFile());
        }catch(Exception e){
            logger.error("Exception while touching files",e);
        }
    }

    public static File[] getFilesInDirectory(String path){
        try {
            File dir = Paths.get(path).toFile();
            return dir.listFiles();
        }catch(InvalidPathException | SecurityException e){
            logger.error("Unable to retrieve list of files");
        }
        return new File[0];
    }

    public static String extractCounty(Path file) {
        try {
            if (file != null) {
                String fileName = file.getFileName().toString();
                if (StringUtils.isNotBlank(fileName) && fileName.length() > WebPageConstants.PAGE_URL.length()) {
                    return fileName.substring(0, fileName.indexOf(WebPageConstants.PAGE_URL));
                }
            }
        } catch (StringIndexOutOfBoundsException | NullPointerException e) {
            logger.error("{} with {}",e.getMessage(),e.getClass(),e);
        }
        return StringUtils.EMPTY;
    }

    public static void parseCsv(String fileName, String path){
        try(Reader in = new FileReader(path+"/"+fileName)) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : records) {
                String subject = record.get("Subject");
                String lastName = record.get("Last Name");
                String firstName = record.get("First Name");
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    public static Path getFilePath(String relativePathName) {
        return Paths.get(WebPageConstants.PATH_TO_PAGE_STORAGE + "/" + relativePathName);
    }
}