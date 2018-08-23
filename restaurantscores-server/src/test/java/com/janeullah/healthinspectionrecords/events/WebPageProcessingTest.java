package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.async.WebPageProcessingAsync;
import com.janeullah.healthinspectionrecords.domain.FileToBeProcessed;
import com.janeullah.healthinspectionrecords.domain.PathVariables;
import com.janeullah.healthinspectionrecords.exceptions.WebPageProcessAsyncException;
import com.janeullah.healthinspectionrecords.util.TestFileUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebPageProcessingTest {

    @InjectMocks
    private WebPageProcessing webPageProcessing;

    @Mock
    private PathVariables pathVariables;

    @Mock
    private WebPageProcessingAsync webPageProcessingAsync;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWebPageProcessingSuccessful() throws WebPageProcessAsyncException {
        File[] files = TestFileUtil.getFilesInDirectory("./src/test/resources/downloads/webpages");
        when(pathVariables.getFilesInDefaultDirectory()).thenReturn(files);


        webPageProcessing.startProcessingOfDownloadedFiles();

        verify(webPageProcessingAsync, times(10)).processWebPage(any(FileToBeProcessed.class));

    }

}
