package com.janeullah.healthinspectionrecords.constants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class PathVariables {

    @Value("${PATH_TO_PAGE_STORAGE}")
    private String pathToPageStorage;

    public File[] getFilesInDefaultDirectory() {
        try {
            File dir = Paths.get(pathToPageStorage).toFile();
            File[] filesInDirectory = dir.listFiles();
            if (filesInDirectory == null) {
                throw new InvalidPathException(pathToPageStorage, pathToPageStorage + " directory is invalid");
            }
            return filesInDirectory;
        } catch (InvalidPathException | SecurityException e) {
            log.error("Unable to retrieve list of files", e);
        }
        return new File[0];
    }

    public Path getFilePath(String relativePathName) {
        return Paths.get(pathToPageStorage + File.separator + relativePathName);
    }

    public File getDefaultFilePath(String fileName) {
        return new File(pathToPageStorage + File.separator + fileName);
    }
}
