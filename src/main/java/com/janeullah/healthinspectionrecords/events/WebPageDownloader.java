package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.async.WebPageRequestAsync;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.util.FilesUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static com.janeullah.healthinspectionrecords.util.ExecutorUtil.EXECUTOR_SERVICE;

/** Author: Jane Ullah Date: 9/17/2016 */
@Slf4j
@Component
public class WebPageDownloader {
  private static final CompletionService<String> webPageDownloadCompletionService =
      new ExecutorCompletionService<>(EXECUTOR_SERVICE);

  //https://stackoverflow.com/questions/10156191/real-life-examples-for-countdownlatch-and-cyclicbarrier/32416323
  //contains count of tasks to be completed. Unrelated to the count of threads being used to perform work
  private static final CountDownLatch COUNT_DOWN_LATCH =
      new CountDownLatch(WebPageConstants.COUNTY_LIST.size());
  private WebPageProcessing webPageProcessing;

  @Value("${DOWNLOAD_OVERRIDE}")
  private boolean isDownloadOverrideEnabled;

  @Value("${DATA_EXPIRATION_IN_DAYS}")
  private String dataExpirationInDays;

  @Autowired
  public WebPageDownloader(WebPageProcessing webPageProcessing) {
    this.webPageProcessing = webPageProcessing;
  }

  private List<WebPageRequestAsync> populateListOfAsyncWebRequestToBeMade() {
    List<WebPageRequestAsync> results = new ArrayList<>();
    Map<String, String> urls = getUrls();
    urls.forEach(
        (key, value) -> results.add(new WebPageRequestAsync(value, key, COUNT_DOWN_LATCH)));
    return results;
  }

  // Return Map of County Name to County URL
  private static Map<String, String> getUrls() {
    Map<String, String> results = new ConcurrentHashMap<>();
    for (String county : WebPageConstants.COUNTY_LIST) {
      results.put(county, String.format(WebPageConstants.URL, county));
    }
    return results;
  }

  /**
   * TODO: Hook this up to logic for checking age of either: i) first - files on disk ii) second -
   * data in datastore
   *
   * @return boolean
   */
  public boolean isDownloadOverrideOrDataExpired() {
    return isDownloadOverrideEnabled || areFilesOlderThanLimit();
  }

  /**
   * http://stackoverflow.com/questions/2064694/how-do-i-find-the-last-modified-file-in-a-directory-in-java
   *
   * @return boolean
   */
  private boolean areFilesOlderThanLimit() {
    try {
      DateTime maxAgeDate = DateTime.now().minus(getMaxExpirationDate().get());
      File[] files = FilesUtil.getFilesInDirectory(WebPageConstants.PATH_TO_PAGE_STORAGE);
      OptionalLong result =
          Stream.of(files)
              .mapToLong(File::lastModified)
              .filter(
                  lastModifiedDateTime ->
                      Long.compare(lastModifiedDateTime, maxAgeDate.getMillis()) < 0)
              .findAny();
      return result.isPresent();
    } catch (Exception e) {
      log.error("Exception while checking for last modified date of files on disk", e);
    }
    return false;
  }

  private AtomicLong getMaxExpirationDate() {
    int daysToExpire = Integer.parseInt(dataExpirationInDays);
    return new AtomicLong((long) daysToExpire * DateTimeConstants.MILLIS_PER_DAY);
  }

  public void initiateDownloadsAndProcessFiles() {
    try {
      asyncDownloadWebPages();
      COUNT_DOWN_LATCH.await();

      log.info("file downloads completed.");
      webPageProcessing.startProcessingOfDownloadedFiles();
    } catch (InterruptedException e) {
      log.error("Interrupt while waiting for downloads to complete", e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * http://stackoverflow.com/questions/4524063/make-simultaneous-web-requests-in-java
   * http://nohack.eingenetzt.com/java/java-executorservice-and-threadpoolexecutor-tutorial/
   * http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
   * http://nohack.eingenetzt.com/java/java-guava-librarys-listeningexecutorservice-tutorial/
   */
  private List<Future<String>> asyncDownloadWebPages() {
    List<WebPageRequestAsync> callablePageRequests =
        Collections.synchronizedList(populateListOfAsyncWebRequestToBeMade());
    List<Future<String>> futures = new ArrayList<>(callablePageRequests.size());
    callablePageRequests.forEach(
        webPageRequest -> {
          futures.add(webPageDownloadCompletionService.submit(webPageRequest));
          log.info("Download request submitted for {}", webPageRequest.getName());
        });
    return futures;
  }
}
