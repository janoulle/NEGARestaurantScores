package com.janeullah.healthinspectionrecords.org.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class ExecutorUtil {
    public final static ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(WebPageConstants.NUMBER_OF_THREADS));
    //public final static ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(executorService);
    private final static Logger logger = Logger.getLogger(ExecutorUtil.class);

    /**
     * http://stackoverflow.com/questions/3269445/executorservice-how-to-wait-for-all-tasks-to-finish?rq=1
     */
    public static void shutDown(){
        try {
            if (!executorService.isShutdown() || !executorService.isTerminated()) {
                logger.info("event=\"shutting down executor\"");
                executorService.shutdown();
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }
        }catch(InterruptedException e){
            logger.error(e);
            executorService.shutdownNow();
        }
    }

    public static int getThreadCount(){
        return WebPageConstants.NUMBER_OF_THREADS;
    }
}
