package com.janeullah.healthinspectionrecords.org.web;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.*;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageDownloader {
    private final static Logger logger = Logger.getLogger(WebPageDownloader.class);
    public final static List<String> COUNTY_LIST = Stream.of(WebPageConstants.COUNTIES.split(",")).collect(Collectors.toList());
    public final static int NUMBER_OF_THREADS = COUNTY_LIST.size();

    public WebPageDownloader() {
        logger.info("event=\"WebPageDownloader initialized\"");
    }

    public static void main(String[] args) {
        WebPageDownloader dwld = new WebPageDownloader();
        dwld.downloadWebPages();
        System.exit(0);
    }

    private static ConcurrentMap<String, String> getUrls() {
        ConcurrentMap<String, String> results = Maps.newConcurrentMap();
        if (StringUtils.isNotBlank(WebPageConstants.COUNTIES) && StringUtils.isNotBlank(WebPageConstants.URL)) {
            COUNTY_LIST.forEach(entry ->
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
    public void downloadWebPages() {
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(NUMBER_OF_THREADS));

        try {
            ConcurrentMap<String, String> urls = getUrls();

            urls.entrySet().forEach(entry -> {
                        ListenableFuture<InputStream> future = executorService.submit(new WebPageRequest(entry.getValue()));
                        Futures.addCallback(future, new FutureCallback<InputStream>() {
                            public void onSuccess(InputStream result) {
                                //cache the data
                                copyStreamToDisk(entry.getKey(), result);
                            }

                            public void onFailure(Throwable thrown) {
                                logger.error(thrown);
                            }
                        });
                    }
            );


            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (SecurityException | InterruptedException | IllegalArgumentException e) {
            logger.error(e);
        } finally {
            if (!executorService.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executorService.shutdownNow();
            System.out.println("shutdown finished");
        }
    }

    private void copyStreamToDisk(String name, InputStream reqStream) {

        try {
            File destinationFile = new File("src/main/resources/downloads/webpages/" + name + ".html");
            FileUtils.copyInputStreamToFile(reqStream, destinationFile);
            if (destinationFile.exists()) {
                System.out.println(name + ".html file created");
            }
        } catch (IOException e) {
            logger.error(e);
        }

    }

    /*DoubleFunction sine = (double x) -> {
        return Math.sin(x);
    };*/


    /*Callable<InputStream> getWebPageInputStream = (String url) -> {
                try {
                    return new URL(url).openStream();
                }
                catch (IOException e) {
                    throw new IllegalStateException("task interrupted", e);
                }
                return null;
    };*/

    public class WebPageRequest implements Callable<InputStream> {

        private String url;

        public WebPageRequest(String url) {
            this.url = url;
        }

        @Override
        public InputStream call() throws Exception {
            return new URL(url).openStream();
        }

    }
}
