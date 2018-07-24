package com.janeullah.healthinspectionrecords.domain;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class PathVariablesTest {

  @Spy private PathVariables pathVariables;

  @Before
  public void setup() {
    ReflectionTestUtils.setField(
        pathVariables,
        "relativePathToPageStorage",
        "." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "downloads" + File.separator + "webpages" + File.separator);
  }

  @Test
  public void testGetFilesInDefaultDirectory_Success() {
    ReflectionTestUtils.setField(pathVariables, "appDataFolder", "");
    File[] files = pathVariables.getFilesInDefaultDirectory();
    assertNotNull(files);
    assertEquals(10, files.length);
  }

  @Test
  public void testGetFilesInDefaultDirectory_Error() {
    ReflectionTestUtils.setField(pathVariables, "appDataFolder", "bad");
    File[] files = pathVariables.getFilesInDefaultDirectory();
    assertNotNull(files);
    assertEquals(0, files.length);
  }
}
