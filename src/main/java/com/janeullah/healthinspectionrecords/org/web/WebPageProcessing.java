package com.janeullah.healthinspectionrecords.org.web;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.janeullah.healthinspectionrecords.org.async.WebPageProcessAsync;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.org.model.Restaurant;
import com.janeullah.healthinspectionrecords.org.util.DatabaseUtil;
import com.janeullah.healthinspectionrecords.org.util.ExecutorUtil;
import com.janeullah.healthinspectionrecords.org.util.WatchDir;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static com.janeullah.healthinspectionrecords.org.util.ExecutorUtil.executorService;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageProcessing {
    private final static Logger logger = Logger.getLogger(WebPageProcessing.class);
    private static ConcurrentMap<String,List<Restaurant>> restaurantsByCounties;
    private static ConcurrentMap<String,Boolean> entriesBeingWatched;
    static CountDownLatch doneSignal = new CountDownLatch(ExecutorUtil.getThreadCount());

    private WatchDir directoryWatcher;


    public WebPageProcessing(){
        logger.info("event=\"WebPageProcessing initialized\"");
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
        processAlreadyDownloadedFiles();
    }

    protected CountDownLatch getDoneSignal(){
        return doneSignal;
    }

    public WatchDir getDirectoryWatcher() {
        return directoryWatcher;
    }

    public void setDirectoryWatcher(WatchDir directoryWatcher) {
        this.directoryWatcher = directoryWatcher;
    }


    /**
     * TODO: figure out best way to reuse executor and shut down when done
     * @param file Path to downloaded file relative
     */
    public static void asyncProcessFile(Path file, ConcurrentMap<String,Boolean> mapOfEntriesBeingWatched){
        try {
            ListenableFuture<List<Restaurant>> future = executorService.submit(new WebPageProcessAsync(file,doneSignal));
            String countyFile = FilenameUtils.getName(file.getFileName().toString());
            mapOfEntriesBeingWatched.put(countyFile,true);
            Futures.addCallback(future, new FutureCallback<List<Restaurant>>() {
                public void onSuccess(List<Restaurant> result) {
                    getRestaurantsByCounties().put(countyFile,result);
                    DatabaseUtil.persistData(countyFile,result);
                }
                public void onFailure(Throwable thrown) {
                    logger.error(thrown);
                }
            });
        } catch (SecurityException e) {
            logger.error(e);
        }
    }

    private void processAlreadyDownloadedFiles(){
        try{
            File dir = directoryWatcher.getPath().toFile();
            File[] files = dir.listFiles();
            if (files != null) {
                Stream.of(files).forEach(file ->
                    asyncProcessFile(file.toPath(), getEntriesBeingWatched())
                );
            }
        }catch(Exception e) {
            logger.error(e);
        }
    }

    public static ConcurrentMap<String,Boolean> getEntriesBeingWatched(){
        if (entriesBeingWatched == null){
            entriesBeingWatched = Maps.newConcurrentMap();
        }
        return entriesBeingWatched;
    }

    public static ConcurrentMap<String,List<Restaurant>> getRestaurantsByCounties(){
        if (restaurantsByCounties == null){
            restaurantsByCounties = Maps.newConcurrentMap();
        }
        return restaurantsByCounties;
    }
}
