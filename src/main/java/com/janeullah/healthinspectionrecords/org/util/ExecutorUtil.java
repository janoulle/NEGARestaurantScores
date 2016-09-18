package com.janeullah.healthinspectionrecords.org.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class ExecutorUtil {
    public final static ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(WebPageConstants.NUMBER_OF_THREADS));
    //public final static ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(executorService);
    private final static Logger logger = Logger.getLogger(ExecutorUtil.class);

    public static void shutDown(){
        if (!executorService.isTerminated()) {
            logger.error("event=\"shutting down executor\"");
            executorService.shutdownNow();
        }
    }
}
