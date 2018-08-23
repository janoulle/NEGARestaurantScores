package com.janeullah.healthinspectionrecords.async;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.domain.PathVariables;
import com.janeullah.healthinspectionrecords.services.external.NorthEastGeorgiaWebPageDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class WebPageDownloadAsync {

    private PathVariables pathVariables;
    private NorthEastGeorgiaWebPageDownloadService northEastGeorgiaWebPageDownloadService;

    @Autowired
    public WebPageDownloadAsync(PathVariables pathVariables,
                                NorthEastGeorgiaWebPageDownloadService northEastGeorgiaWebPageDownloadService) {
        this.northEastGeorgiaWebPageDownloadService = northEastGeorgiaWebPageDownloadService;
        this.pathVariables = pathVariables;
    }

    //todo: investigate jdk way of coping html to file
    //https://www.baeldung.com/guava-write-to-file-read-from-file
    @Async
    public CompletableFuture<Boolean> downloadWebPage(String countyName) {
        try {
            log.info("Download request submitted for {}", countyName);
            String webpageHtml = northEastGeorgiaWebPageDownloadService.getWebPage(countyName);
            File destinationFile = pathVariables.getDefaultFilePath(countyName + WebPageConstants.PAGE_URL);
            CharSink sink = Files.asCharSink(destinationFile, Charsets.UTF_8);
            sink.write(webpageHtml);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to download and/or write webpage for county={} to disk", countyName, e);
        }
        return CompletableFuture.completedFuture(false);
    }

}
