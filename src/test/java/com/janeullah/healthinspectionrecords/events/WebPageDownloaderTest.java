package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.constants.PathVariables;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import org.apache.commons.collections4.map.HashedMap;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class WebPageDownloaderTest {

    public static final String IS_DOWNLOAD_OVERRIDE_ENABLED = "isDownloadOverrideEnabled";
    @InjectMocks
    private WebPageDownloader webPageDownloader;

    @Mock
    private WebPageProcessing webPageProcessing;

    @Mock
    private PathVariables pathVariables;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNoDownloadNeededScenario() {
        ReflectionTestUtils.setField(webPageDownloader, IS_DOWNLOAD_OVERRIDE_ENABLED, false);
        ReflectionTestUtils.setField(webPageDownloader, "dataExpirationInDays", "7");
        when(pathVariables.getFilesInDefaultDirectory()).thenReturn(new File[1]);

        assertFalse(webPageDownloader.isDownloadOverrideOrDataExpired());
    }

    @Test
    public void testDownloadNeededScenarioDueToOverrideSet() {
        ReflectionTestUtils.setField(webPageDownloader, IS_DOWNLOAD_OVERRIDE_ENABLED, true);

        assertTrue(webPageDownloader.isDownloadOverrideOrDataExpired());
    }

    @Test
    public void testDownloadNeededScenarioDueToEmptyFolder() {
        ReflectionTestUtils.setField(webPageDownloader, IS_DOWNLOAD_OVERRIDE_ENABLED, false);
        when(pathVariables.getFilesInDefaultDirectory()).thenReturn(new File[0]);

        assertTrue(webPageDownloader.isDownloadOverrideOrDataExpired());
    }

    @Test
    public void testDownloadNeededScenarioDueToExpiredFiles() {
        ReflectionTestUtils.setField(webPageDownloader, IS_DOWNLOAD_OVERRIDE_ENABLED, false);
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


    @Test
    public void testInitiateDownloadsAndProcessFilesWithBadUrl() {
        //faking the url to hit
        Map<String, String> map = new HashedMap<>();
        for (String county : WebPageConstants.COUNTY_LIST) {
            map.put(county, "http://xyx.com" + county);
        }

        ReflectionTestUtils.setField(webPageDownloader, "mapOfUrlsToDownloads", map);

        //Commented out for unhappy path (this method never gets called) as an unnecessary stubbing
        //when(pathVariables.getDefaultFilePath(anyString())).thenReturn(new File("downloads/test/file.html"));

        webPageDownloader.initiateDownloadsAndProcessFiles();

        verify(webPageProcessing, times(1)).startProcessingOfDownloadedFiles();
    }
}
