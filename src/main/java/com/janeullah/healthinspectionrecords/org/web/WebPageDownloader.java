package com.janeullah.healthinspectionrecords.org.web;

import com.google.common.collect.Maps;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageDownloader {
    private final static Logger logger = Logger.getLogger(WebPageDownloader.class);

    public WebPageDownloader(){
        logger.info("event=\"WebPageDownloader initialized\"");
    }

    public static void main(String[] args){
        WebPageDownloader dwld = new WebPageDownloader();
        dwld.downloadWebPages();
    }

    private static List<String> getCounties(){
        return Stream.of(WebPageConstants.COUNTIES.split(",")).collect(Collectors.toList());
    }

    private static ConcurrentMap<String,String> getUrls(){
        ConcurrentMap<String,String> results = Maps.newConcurrentMap();
        if (StringUtils.isNotBlank(WebPageConstants.COUNTIES) && StringUtils.isNotBlank(WebPageConstants.URL)){
            getCounties().forEach(entry ->
                    results.put(entry,String.format(WebPageConstants.URL,entry))
            );
        }
        return results;
    }

    /**
     * http://stackoverflow.com/questions/4524063/make-simultaneous-web-requests-in-java
     */
    public void downloadWebPages(){
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            ConcurrentMap<String,String> urls = getUrls();
            ConcurrentMap<String,Future<InputStream>> requestStreams = Maps.newConcurrentMap();
            //ConcurrentMap<String, WebPageRequest> futures = Maps.newConcurrentMap();

            urls.entrySet().forEach(entry -> {
                WebPageRequest request = new WebPageRequest(entry.getValue());
                requestStreams.put(entry.getKey(), executorService.submit(request));
                //futures.put(entry.getKey(),request);
                    }
            );

            requestStreams.entrySet().forEach(stream -> {
                Future<InputStream> req = stream.getValue();
                if (req.isDone()){
                    try (InputStream reqStream = req.get()) {
                        File destinationFile = new File("downloads/webpages/" + stream.getKey()+".html");
                        FileUtils.copyInputStreamToFile(reqStream, destinationFile);
                        if (destinationFile.exists()){
                            System.out.println("file created");
                        }
                    }catch(IOException | InterruptedException | ExecutionException e){
                        logger.error(e);
                    }
                }
            });

            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }catch(SecurityException | InterruptedException | IllegalArgumentException e){
            logger.error(e);
        }finally {
            if (!executorService.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executorService.shutdownNow();
            System.out.println("shutdown finished");
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
