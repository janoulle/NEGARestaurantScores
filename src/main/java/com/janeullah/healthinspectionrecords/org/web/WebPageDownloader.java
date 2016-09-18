package com.janeullah.healthinspectionrecords.org.web;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.janeullah.healthinspectionrecords.org.async.WebPageRequest;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;

import static com.janeullah.healthinspectionrecords.org.util.ExecutorUtil.executorService;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageDownloader {
    private final static Logger logger = Logger.getLogger(WebPageDownloader.class);

    public WebPageDownloader() {
        logger.info("event=\"WebPageDownloader initialized\"");
    }

    public void executeProcess(){
        if (isDataExpired()) {
            downloadWebPages();
        }
    }

    private static ConcurrentMap<String, String> getUrls() {
        ConcurrentMap<String, String> results = Maps.newConcurrentMap();
        if (StringUtils.isNotBlank(WebPageConstants.COUNTIES) && StringUtils.isNotBlank(WebPageConstants.URL)) {
            WebPageConstants.COUNTY_LIST.forEach(entry ->
                    results.put(entry, String.format(WebPageConstants.URL, entry))
            );
        }
        return results;
    }

    /**
     * http://stackoverflow.com/questions/4524063/make-simultaneous-web-requests-in-java
     * http://nohack.eingenetzt.com/java/java-executorservice-and-threadpoolexecutor-tutorial/
     * http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
     * http://nohack.eingenetzt.com/java/java-guava-librarys-listeningexecutorservice-tutorial/
     */
    private void downloadWebPages() {
        try {
            ConcurrentMap<String, String> urls = getUrls();

            urls.entrySet().forEach(entry -> {
                        ListenableFuture<InputStream> future = executorService.submit(new WebPageRequest(entry.getValue()));
                        Futures.addCallback(future, new FutureCallback<InputStream>() {
                            public void onSuccess(InputStream result) {
                                copyStreamToDisk(entry.getKey(), result);
                            }
                            public void onFailure(Throwable thrown) {
                                logger.error(thrown);
                            }
                        });
                    }
            );
        } catch (SecurityException e) {
            logger.error(e);
        }
    }

    private void copyStreamToDisk(String name, InputStream reqStream) {
        try {
            String fileName = name + WebPageConstants.PAGE_URL;
            File destinationFile = new File("src/main/resources/downloads/webpages/" + fileName);
            FileUtils.copyInputStreamToFile(reqStream, destinationFile);
            if (destinationFile.exists()) {
                logger.info("event=\"" + fileName + " created\"");
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * TODO: Hook this up to logic for checking age of either:
     *  files on disk
     *  data in datastore
     * @return boolean
     */
    public static boolean isDataExpired(){
        return true;
    }
}
