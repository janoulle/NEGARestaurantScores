package com.janeullah.healthinspectionrecords.org.web;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
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

import static com.janeullah.healthinspectionrecords.org.util.ExecutorUtil.executorService;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageProcessing {
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

    /**
     * TODO: figure out best way to reuse executor and shut down when done
     * @param fileName Path to downloaded file relative
     */
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
        } catch (SecurityException e) {
            logger.error(e);
        }
    }
}
