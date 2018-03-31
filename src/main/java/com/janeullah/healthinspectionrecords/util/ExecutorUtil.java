package com.janeullah.healthinspectionrecords.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * http://stackoverflow.com/questions/4912228/when-should-i-use-a-completionservice-over-an-executorservice
 * Author: Jane Ullah
 * Date:  9/18/2016
 */
@Slf4j
public class ExecutorUtil {
    public static final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(WebPageConstants.NUMBER_OF_THREADS));

    private ExecutorUtil() {
    }

    /**
     * http://stackoverflow.com/questions/3269445/executorservice-how-to-wait-for-all-tasks-to-finish?rq=1
     */
    public static void shutDown() {
        try {
            if (!executorService.isShutdown() || !executorService.isTerminated()) {
                log.info("event=\"shutting down executor\"");
                executorService.shutdown();
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            log.error("InterruptedException during executor shut down", e);
            Thread.currentThread().interrupt();
        }
    }

    //http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    log.error("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static int getThreadCount() {
        return WebPageConstants.NUMBER_OF_THREADS;
    }
}
