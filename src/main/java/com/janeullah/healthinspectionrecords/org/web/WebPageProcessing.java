package com.janeullah.healthinspectionrecords.org.web;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.*;
import com.janeullah.healthinspectionrecords.org.async.WebPageProcess;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.org.util.DatabaseUtil;
import com.janeullah.healthinspectionrecords.org.util.WatchDir;
import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageProcessing {
    final static ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(WebPageConstants.NUMBER_OF_THREADS));
    private final static Logger logger = Logger.getLogger(WebPageProcessing.class);
    private WatchDir directoryWatcher;


    public WebPageProcessing(){
        setupWatcher();
    }

    private void setupWatcher(){
        try{
            Path dir = Paths.get(WebPageConstants.PATH_TO_PAGE_STORAGE);
            directoryWatcher = new WatchDir(dir, false);
        }catch(IOException e){
            logger.error(e);
        }
    }

    public void executeProcess(){
        if (directoryWatcher == null) {
            setupWatcher();
        }
        directoryWatcher.executeProcess();
    }

    public static void asyncProcessFile(Path fileName){
        try {
            ListenableFuture<List<Elements>> future = executorService.submit(new WebPageProcess(fileName));
            Futures.addCallback(future, new FutureCallback<List<Elements>>() {
                public void onSuccess(List<Elements> result) {
                    DatabaseUtil.persistData(result);
                }
                public void onFailure(Throwable thrown) {
                    logger.error(thrown);
                }
            });
            executorService.shutdown();
        } catch (SecurityException e) {
            logger.error(e);
        } finally {
            if (!executorService.isTerminated()) {
                logger.error("event=\"cancel non-finished tasks\"");
                executorService.shutdownNow();
            }
            logger.info("event=\"shutdown finished\"");
        }
    }

    private ConcurrentMap<String,Path> getDownloadedFiles(){
        ConcurrentMap<String,Path> filePaths = Maps.newConcurrentMap();

        try{
            Path pathToDir = directoryWatcher.getPath();
            WebPageConstants.COUNTY_LIST.forEach(county -> {
                Path newPath = pathToDir.resolve(county + ".html");
                filePaths.put(county,newPath);
            });

        }catch(Exception e) {
            logger.error(e);
        }
        return filePaths;
    }
}
