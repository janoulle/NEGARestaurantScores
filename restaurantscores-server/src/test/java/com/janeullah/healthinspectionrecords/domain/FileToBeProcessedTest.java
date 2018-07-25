package com.janeullah.healthinspectionrecords.domain;

import com.janeullah.healthinspectionrecords.constants.counties.NEGACounties;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.janeullah.healthinspectionrecords.util.TestFileUtil.FILES;
import static org.junit.Assert.assertEquals;

public class FileToBeProcessedTest {

  @Test
  public void testCountyNameParsing_Successful() {
    List<String> actualResults =
        Stream.of(FILES)
            .map(file -> new FileToBeProcessed(file.toPath()).getCountyName())
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
    assertEquals(NEGACounties.getCountOfCounties(), actualResults.size());
    }

    @Test
    public void testCountyNameParsing_Fail() {
      Path path = new File("a").toPath();
        FileToBeProcessed fileToBeProcessed = new FileToBeProcessed(path);
        assertEquals("", fileToBeProcessed.getCountyName());
    }
}
