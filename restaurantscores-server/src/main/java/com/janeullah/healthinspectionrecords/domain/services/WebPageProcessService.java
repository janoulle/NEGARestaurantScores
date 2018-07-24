package com.janeullah.healthinspectionrecords.domain.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.janeullah.healthinspectionrecords.async.WebPageProcessAsync;
import com.janeullah.healthinspectionrecords.domain.FileToBeProcessed;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.janeullah.healthinspectionrecords.util.ExecutorUtil.EXECUTOR_SERVICE;

@Slf4j
@Service
public class WebPageProcessService {

  private RestaurantPersistenceService restaurantPersistenceService;

  @Autowired
  public WebPageProcessService(RestaurantPersistenceService restaurantPersistenceService) {
    this.restaurantPersistenceService = restaurantPersistenceService;
  }

  public void submitFileForProcessing(@NotNull Path file, CountDownLatch countDownLatch) {

    if (file.getFileName() == null) {
      log.error("Invalid file path passed in");
      countDownLatch.countDown();
      return;
    }

    FileToBeProcessed fileToBeProcessed = new FileToBeProcessed(file);
    ListenableFuture<List<Restaurant>> future =
        EXECUTOR_SERVICE.submit(new WebPageProcessAsync(fileToBeProcessed));
    Futures.addCallback(
        future,
        new WebPageProcessRequestCallBack(fileToBeProcessed.getCountyName(), countDownLatch),
        EXECUTOR_SERVICE);
  }

  public void waitForAllProcessing(CountDownLatch countDownLatch) throws InterruptedException {
    // wait for processing to complete
    countDownLatch.await();
  }

  private class WebPageProcessRequestCallBack implements FutureCallback<List<Restaurant>> {
    private String countyFile;
    private CountDownLatch countDownLatch;

    WebPageProcessRequestCallBack(String county, CountDownLatch countDownLatch) {
      this.countyFile = county;
      this.countDownLatch = countDownLatch;
    }

    @Override
    public void onSuccess(List<Restaurant> result) {
      log.info("Web Page Processing completed for county: {} size: {}", countyFile, result.size());
      restaurantPersistenceService.saveAll(result);
      countDownLatch.countDown();
    }

    @Override
    public void onFailure(Throwable thrown) {
      log.error("Failure during Future callback for async file processing", thrown);
      countDownLatch.countDown();
    }
  }
}


