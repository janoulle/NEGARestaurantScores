package com.janeullah.healthinspectionrecords.services;

import com.janeullah.healthinspectionrecords.async.WebPageRequestAsync;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.util.ExecutorUtil;
import com.janeullah.healthinspectionrecords.util.FilesUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static com.janeullah.healthinspectionrecords.constants.WebPageConstants.DOWNLOAD_OVERRIDE;
import static com.janeullah.healthinspectionrecords.util.ExecutorUtil.executorService;

/**
 * Author: Jane Ullah
 * Date:  9/17/2016
 */
@Component
public class WebPageDownloader {
    private static final Logger logger = LoggerFactory.getLogger(WebPageDownloader.class);
    private static CountDownLatch doneSignal = new CountDownLatch(ExecutorUtil.getThreadCount());
    private static final List<WebPageRequestAsync> callablePageRequests = Collections.synchronizedList(populateListOfAsyncWebRequestToBeMade());
    private static final CompletionService<String> webPageDownloadCompletionService = new ExecutorCompletionService<>(executorService);

    @Autowired
    private WebPageProcessing webPageProcessing;

    public void initiateDownloadsAndProcessFiles(){
        waitForCompletion(asyncDownloadWebPages());
    }

    private void waitForCompletion(List<Future<String>> futures) {
        try {
            int remainingFutures = futures.size();
            while (remainingFutures > 0) {
                Future<String> completedFuture = webPageDownloadCompletionService.take();
                String relativePathName = completedFuture.get();
                if (StringUtils.isNotBlank(relativePathName)) {
                    logger.info("{} was successfully downloaded.",relativePathName);
                    webPageProcessing.asyncProcessFile(FilesUtil.getFilePath(relativePathName));
                }else{
                    logger.error("Failed to download successfully.");
                }
                remainingFutures--;
            }
        }catch(InterruptedException | ExecutionException e){
            logger.error("Error retrieving future from service",e);
        }
    }

    private static List<WebPageRequestAsync> populateListOfAsyncWebRequestToBeMade(){
        List<WebPageRequestAsync> results = new ArrayList<>();
        Map<String, String> urls = getUrls();
        urls.forEach((key, value) -> results.add(new WebPageRequestAsync(value,key,doneSignal)));
        return results;
    }

    //Return Map of County Name to County URL
    private static Map<String, String> getUrls() {
        Map<String, String> results = new ConcurrentHashMap<>();
        for (String county : WebPageConstants.COUNTY_LIST) {
            results.put(county, String.format(WebPageConstants.URL, county));
        }
        return results;
    }

    /**
     * http://stackoverflow.com/questions/4524063/make-simultaneous-web-requests-in-java
     * http://nohack.eingenetzt.com/java/java-executorservice-and-threadpoolexecutor-tutorial/
     * http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
     * http://nohack.eingenetzt.com/java/java-guava-librarys-listeningexecutorservice-tutorial/
     */
    private List<Future<String>> asyncDownloadWebPages() {
        List<Future<String>> futures = new ArrayList<>(callablePageRequests.size());
        callablePageRequests.forEach(webPageRequest ->
                futures.add(webPageDownloadCompletionService.submit(webPageRequest)));
        return futures;
    }

    /**
     * TODO: Hook this up to logic for checking age of either:
     * i) first - files on disk
     * ii) second - data in datastore
     *
     * @return boolean
     */
    public static boolean isDataExpired() {
        return DOWNLOAD_OVERRIDE || !areFilesMoreRecentThanLimit();
    }

    /**
     * http://stackoverflow.com/questions/2064694/how-do-i-find-the-last-modified-file-in-a-directory-in-java
     *
     * @return boolean
     */
    private static boolean areFilesMoreRecentThanLimit() {
        try {
            DateTime cal = DateTime.now().minus(getMaxExpirationDate().get());
            File[] files = FilesUtil.getFilesInDirectory(WebPageConstants.PATH_TO_PAGE_STORAGE);
            OptionalLong result = Stream.of(files)
                    .mapToLong(File::lastModified)
                    .filter(lastModification -> Long.compare(lastModification, cal.getMillis()) > 0)
                    .findAny();
            return result.isPresent();
        } catch (Exception e) {
            logger.error("Exception while checking for areFilesMoreRecentThanLimit", e);
        }
        return false;
    }

    private static AtomicLong getMaxExpirationDate() {
        int daysToExpire = Integer.parseInt(WebPageConstants.DATA_EXPIRATION_IN_DAYS);
        return new AtomicLong(daysToExpire * DateTimeConstants.MILLIS_PER_DAY);
    }

}
