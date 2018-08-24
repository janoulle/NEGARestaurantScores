package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.async.WebPageDownloadAsync;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.constants.counties.NEGACounties;
import com.janeullah.healthinspectionrecords.domain.PathVariables;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * ttps://stackoverflow.com/questions/10156191/real-life-examples-for-countdownlatch-and-cyclicbarrier/32416323
 * Author: Jane Ullah Date: 9/17/2016
 */
@Slf4j
@Component
public class WebPageDownloader {
    private static Map<String, String> mapOfUrlsToDownloads;

    // Return Map of County Name to County URL
    static {
        mapOfUrlsToDownloads = new ConcurrentHashMap<>();
        for (String county : NEGACounties.getAllNEGACounties()) {
            mapOfUrlsToDownloads.put(county, String.format(WebPageConstants.URL, county));
        }
    }

    private WebPageDownloadAsync webPageDownloadAsync;
    private WebPageProcessing webPageProcessing;
    private PathVariables pathVariables;

    @Value("${DOWNLOAD_OVERRIDE}")
    private boolean isDownloadOverrideEnabled;

    @Value("${DATA_EXPIRATION_IN_DAYS}")
    private String dataExpirationInDays;

    @Value("${USER_AGENT}")
    private String userAgent;

    @Autowired
    public WebPageDownloader(WebPageProcessing webPageProcessing, PathVariables pathVariables, WebPageDownloadAsync webPageDownloadAsync) {
        this.webPageProcessing = webPageProcessing;
        this.pathVariables = pathVariables;
        this.webPageDownloadAsync = webPageDownloadAsync;
    }

    /**
     * Downloads the webpages if the env variable 'DOWNLOAD_OVERRIDE' is set to true OR in the event that
     * there are no files to process or files present are older than the limit set 'DATA_EXPIRATION_IN_DAYS'
     *
     * @return boolean
     */
    public boolean isDownloadOverrideOrDataExpired() {
        return isDownloadOverrideEnabled || (isFolderEmpty() || areFilesOlderThanLimit());
    }

    private boolean isFolderEmpty() {
        return pathVariables.getFilesInDefaultDirectory().length == 0;
    }

    /**
     * http://stackoverflow.com/questions/2064694/how-do-i-find-the-last-modified-file-in-a-directory-in-java
     *
     * @return boolean
     */
    private boolean areFilesOlderThanLimit() {
        try {
            DateTime maxAgeDate = DateTime.now().minus(getMaxExpirationDate().get());
            File[] files = pathVariables.getFilesInDefaultDirectory();
            OptionalLong result =
                    Stream.of(files)
                            .filter(Objects::nonNull)
                            .mapToLong(File::lastModified)
                            .filter(
                                    lastModifiedDateTime ->
                                            Long.compare(lastModifiedDateTime, maxAgeDate.getMillis()) < 0)
                            .findAny();
            return result.isPresent();
        } catch (Exception e) {
            log.error("Exception while checking for last modified date of files on disk", e);
        }
        return false;
    }

    private AtomicLong getMaxExpirationDate() {
        int daysToExpire = Integer.parseInt(dataExpirationInDays);
        return new AtomicLong((long) daysToExpire * DateTimeConstants.MILLIS_PER_DAY);
    }

    // TODO: include some way to verify the downloads were actually successful e.g. via callBacks
    void initiateDownloadsAndProcessFiles() {

        springAsyncDownloadWebPages();

        webPageProcessing.startProcessingOfDownloadedFiles();
    }

    private void springAsyncDownloadWebPages() {
        List<CompletableFuture<Boolean>> downloads = new ArrayList<>();
        for (String countyName : mapOfUrlsToDownloads.keySet()) {
            downloads.add(webPageDownloadAsync.downloadWebPage(countyName));
        }

        CompletableFuture.allOf(downloads.toArray(new CompletableFuture[0])).join();
        log.info("event=file_download message=\"file downloads completed.\"");
    }
}
