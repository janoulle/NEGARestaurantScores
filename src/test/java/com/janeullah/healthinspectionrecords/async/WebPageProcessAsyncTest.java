package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.exceptions.WebPageProcessAsyncException;
import com.janeullah.healthinspectionrecords.util.TestFileUtil;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class WebPageProcessAsyncTest {

  private static final File[] FILES =
      TestFileUtil.getFilesInDirectory("./src/test/resources/downloads/webpages");

  @Test
  public void testWebPageProcess_Success() throws WebPageProcessAsyncException {
    Arrays.sort(FILES, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    Map<String, Integer> fileAndSize = new HashMap<>();
    fileAndSize.put("Barrow", 155);
    fileAndSize.put("Clarke", 528);
    fileAndSize.put("Elbert", 43);
    fileAndSize.put("Greene", 75);
    fileAndSize.put("Jackson", 150);
    fileAndSize.put("Madison", 38);
    fileAndSize.put("Morgan", 63);
    fileAndSize.put("Oconee", 102);
    fileAndSize.put("Oglethorpe", 16);
    fileAndSize.put("Walton", 167);

    for (File file : FILES) {
      Path path = file.toPath();
      FileToBeProcessed fileToBeProcessed = new FileToBeProcessed(path);
      WebPageProcessAsync webPageProcessAsync =
          new WebPageProcessAsync(fileToBeProcessed.getCountyName(), path);
      List<Restaurant> results = webPageProcessAsync.call();
      assertThat(results, hasSize(fileAndSize.get(fileToBeProcessed.getCountyName())));
    }
  }

  @Test(expected = WebPageProcessAsyncException.class)
  public void testWebPageProcess_ExceptionThrow() throws WebPageProcessAsyncException {
      Path nonExistentFile = new File("a").toPath();
      new WebPageProcessAsync("", nonExistentFile).call();
  }
}
