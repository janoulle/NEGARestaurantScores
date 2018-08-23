package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.async.WebPageProcessingAsync;
import com.janeullah.healthinspectionrecords.domain.FileToBeProcessed;
import com.janeullah.healthinspectionrecords.domain.PathVariables;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Author: Jane Ullah Date: 9/17/2016
 */
@Slf4j
@Component
public class WebPageProcessing {
    private PathVariables pathVariables;
    private WebPageProcessingAsync webPageProcessingAsync;

    @Autowired
    public WebPageProcessing(PathVariables pathVariables, WebPageProcessingAsync webPageProcessingAsync) {
        this.pathVariables = pathVariables;
        this.webPageProcessingAsync = webPageProcessingAsync;
    }

    void startProcessingOfDownloadedFiles() {
        try {
            log.info("event=file_processing message=\"Initiating processing of downloaded files\"");
            File[] files = pathVariables.getFilesInDefaultDirectory();

            List<CompletableFuture<List<Restaurant>>> results = new ArrayList<>();
            for (File file : files) {
                results.add(webPageProcessingAsync.processWebPage(new FileToBeProcessed(file.toPath())));
            }

            CompletableFuture.allOf(results.toArray(new CompletableFuture[0])).join();
            log.info("event=file_download message=\"file processing completed.\"");

        } catch (Exception e) {
            log.error("Exception in startProcessingOfDownloadedFiles", e);
        }
    }
}
