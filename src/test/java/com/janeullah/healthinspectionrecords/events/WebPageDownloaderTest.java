package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.constants.PathVariables;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebPageDownloaderTest {

    @InjectMocks
    private WebPageDownloader webPageDownloader;

    @Mock
    private WebPageProcessing webPageProcessing;

    @Mock
    private PathVariables pathVariables;

    @Test
    public void testNoDownloadNeededScenario() {
        ReflectionTestUtils.setField(webPageDownloader, "isDownloadOverrideEnabled", false);
        ReflectionTestUtils.setField(webPageDownloader, "dataExpirationInDays", "7");
        when(pathVariables.getFilesInDefaultDirectory()).thenReturn(new File[1]);

        assertFalse(webPageDownloader.isDownloadOverrideOrDataExpired());
    }

    @Test
    public void testDownloadNeededScenarioDueToOverrideSet() {
        ReflectionTestUtils.setField(webPageDownloader, "isDownloadOverrideEnabled", true);

        assertTrue(webPageDownloader.isDownloadOverrideOrDataExpired());
    }

    @Test
    public void testDownloadNeededScenarioDueToEmptyFolder() {
        ReflectionTestUtils.setField(webPageDownloader, "isDownloadOverrideEnabled", false);
        when(pathVariables.getFilesInDefaultDirectory()).thenReturn(new File[0]);

        assertTrue(webPageDownloader.isDownloadOverrideOrDataExpired());
    }

    @Test
    public void testDownloadNeededScenarioDueToExpiredFiles() {
        ReflectionTestUtils.setField(webPageDownloader, "isDownloadOverrideEnabled", false);
        ReflectionTestUtils.setField(webPageDownloader, "dataExpirationInDays", "1");

        File[] files = new File[1];
        DateTime lastModifiedTime = new DateTime();
        lastModifiedTime.minusDays(2);

        File file = new File("testfile.txt");
        file.setLastModified(lastModifiedTime.getMillis());
        files[0] = file;

        when(pathVariables.getFilesInDefaultDirectory()).thenReturn(files);

        assertTrue(webPageDownloader.isDownloadOverrideOrDataExpired());
    }

}
