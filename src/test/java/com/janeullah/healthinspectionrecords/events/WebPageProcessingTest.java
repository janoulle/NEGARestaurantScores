package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.async.WebPageProcessService;
import com.janeullah.healthinspectionrecords.constants.PathVariables;
import com.janeullah.healthinspectionrecords.util.FileHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebPageProcessingTest {

    @InjectMocks
    private WebPageProcessing webPageProcessing;

    @Mock
    private PathVariables pathVariables;

    @Mock
    private WebPageProcessService webPageProcessService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Ignore("Figure out how to handle the latch blocking execution from completing for test")
    @Test
    public void testWebPageProcessingSuccessful() {
        File[] files = FileHelper.getFilesInDirectory("/downloads/webpages");
        when(pathVariables.getFilesInDefaultDirectory()).thenReturn(files);

        webPageProcessing.startProcessingOfDownloadedFiles();
        verify(webPageProcessService, times(10)).submitFileForProcessing(any(Path.class),any(CountDownLatch.class));
    }
}
