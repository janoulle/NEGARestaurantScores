package com.janeullah.healthinspectionrecords.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * https://stackoverflow.com/questions/18247669/read-directory-inside-jar-with-inputstreamreader *
 * http://www.uofr.net/~greg/java/get-resource-listing.html
 * https://stackoverflow.com/questions/18055189/why-is-my-uri-not-hierarchical
 * https://coderanch.com/t/568837/java/Reading-directory-files-InputStream *
 * https://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file *
 * https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file
 */
@Slf4j
@Component
public class PathVariables {

  @Value("${RELATIVE_PATH_TO_PAGE_STORAGE}")
  private String relativePathToPageStorage;

  @Value("${CATALINA_HOME}")
  private String catalinaHome;

  /**
   * Get list of files in the configured directory.
   * @return Array of File objects.
   */
  public File[] getFilesInDefaultDirectory() {
    try {

      File original = Paths.get(getPath()).toFile();
      File[] filesInDirectory = original.listFiles();
      if (filesInDirectory != null) {
        return filesInDirectory;
      }

      log.error("Failed to fetch files in directory");
    } catch (InvalidPathException | SecurityException e) {
      log.error("Failed to load directory with {] ", getPath(), e);
    }
    return new File[0];
  }

  public File getDefaultFilePath(String fileName) {
    return new File(getPath() + File.separator + fileName);
  }

  private String getPath() {
    return catalinaHome + File.separator + relativePathToPageStorage;
  }
}
