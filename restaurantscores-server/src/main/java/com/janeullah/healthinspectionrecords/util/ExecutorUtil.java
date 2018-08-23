package com.janeullah.healthinspectionrecords.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.janeullah.healthinspectionrecords.constants.counties.NEGACounties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * http://stackoverflow.com/questions/4524063/make-simultaneous-web-requests-in-java
 * http://nohack.eingenetzt.com/java/java-executorservice-and-threadpoolexecutor-tutorial/
 * http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
 * http://nohack.eingenetzt.com/java/java-guava-librarys-listeningexecutorservice-tutorial/
 * http://stackoverflow.com/questions/4912228/when-should-i-use-a-completionservice-over-an-executorservice
 * Author: Jane Ullah Date: 9/18/2016
 */
@Slf4j
public class ExecutorUtil {

    public static final int NUMBER_OF_THREADS = NEGACounties.getCountOfCounties() / 2;
    public static final ListeningExecutorService EXECUTOR_SERVICE =
            MoreExecutors.listeningDecorator(
                    Executors.newFixedThreadPool(NUMBER_OF_THREADS));

    private ExecutorUtil() {
    }

    /**
     * http://stackoverflow.com/questions/3269445/executorservice-how-to-wait-for-all-tasks-to-finish?rq=1
     */
    public static void shutdown() {
        try {
            if (!EXECUTOR_SERVICE.isShutdown() || !EXECUTOR_SERVICE.isTerminated()) {
                log.info("event=\"shutting down executor\"");
                EXECUTOR_SERVICE.shutdown();
                EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            log.error("InterruptedException during executor shut down", e);
            Thread.currentThread().interrupt();
        }
    }

    // http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) log.error("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

}
