package com.janeullah.healthinspectionrecords.util;

import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

/**
 * http://www.vogella.com/tutorials/JavaIO/article.html#javaio_writefile Author: Jane Ullah Date:
 * 9/17/2016
 */
@Slf4j
public class FilesUtil {

  private FilesUtil() {}

  public static String extractCounty(Path file) {
    try {
      if (file != null) {
        String fileName = file.getFileName().toString();
        if (StringUtils.isNotBlank(fileName)
            && fileName.length() > WebPageConstants.PAGE_URL.length()) {
          return fileName.substring(0, fileName.indexOf(WebPageConstants.PAGE_URL));
        }
      }
    } catch (StringIndexOutOfBoundsException | NullPointerException e) {
      log.error("{} with {}", e.getMessage(), e.getClass(), e);
    }
    return StringUtils.EMPTY;
  }
}
