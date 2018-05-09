package com.janeullah.healthinspectionrecords.constants;

import com.janeullah.healthinspectionrecords.constants.counties.NEGACounties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public class PathVariables {

  @Value("${RELATIVE_PATH_TO_PAGE_STORAGE}")
  private String relativePathToPageStorage;

  @Value("${CATALINA_HOME}")
  private String catalinaHome;

  /**
   * https://stackoverflow.com/questions/18055189/why-is-my-uri-not-hierarchical
   *
   * @return
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

  /**
   * https://stackoverflow.com/questions/18247669/read-directory-inside-jar-with-inputstreamreader
   * http://www.uofr.net/~greg/java/get-resource-listing.html
   *
   * @return
   */
  private File[] getFilesInDir() {

    // if null, fall back to fetching the exact resources that should be present
    List<String> fileResourceNames = NEGACounties.getExpectedFilesInDirectory();
    File[] filesInDirectory = new File[fileResourceNames.size()];
    for (int i = 0; i < fileResourceNames.size(); i++) {
      String fileResource = fileResourceNames.get(i);
      try (InputStream inputStream =
          PathVariables.class.getResourceAsStream(relativePathToPageStorage + fileResource)) {

        File file = new File("");
        OutputStream outputStream = new FileOutputStream(file);

        IOUtils.copy(inputStream, outputStream);
        filesInDirectory[i] = file;
      } catch (IllegalArgumentException | IOException e) {
        log.error("Failed to read in resource file with name={}", fileResource, e);
      }
    }

    return filesInDirectory;
  }

  /**
   * https://coderanch.com/t/568837/java/Reading-directory-files-InputStream
   * https://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file
   * https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file
   */
  private void readingFromJar() {
    // attempt to read from jar
    CodeSource src = PathVariables.class.getProtectionDomain().getCodeSource();
    if (src == null) {
      log.error("Unable to fetch code source");
      return;
    }
    URL jar = src.getLocation();
    try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
      for (ZipEntry nextEntry = zip.getNextEntry();
          nextEntry != null;
          nextEntry = zip.getNextEntry()) {
        String name = nextEntry.getName();
        if (name.contains("RESOURCE_NAME")) {
          // grab the file
          File file = new File(name);
          return;
        }
      }
    } catch (IOException io) {
      log.error("Exception while reading jar file", io);
    }
  }

  public Path getFilePath(String relativePathName) {
    return Paths.get(
        catalinaHome
            + File.separator
            + relativePathToPageStorage
            + File.separator
            + relativePathName);
  }

  public File getDefaultFilePath(String fileName) {
    return new File(getPath() + File.separator + fileName);
  }

  private String getPath() {
    return catalinaHome + File.separator + relativePathToPageStorage;
  }
}
