package com.janeullah.healthinspectionrecords.async;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.janeullah.healthinspectionrecords.util.ExecutorUtil.EXECUTOR_SERVICE;

@Slf4j
@Service
public class WebPageProcessService {

  private RestaurantRepository restaurantRepository;

  @Autowired
  public WebPageProcessService(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  public void submitFileForProcessing(@NotNull
      Path file, CountDownLatch countDownLatch) {
    try {
      if (file.getFileName() == null || StringUtils.isBlank(file.getFileName().toString())) {
        return;
      }

      String countyFile = FilenameUtils.getName(file.getFileName().toString());
      Preconditions.checkArgument(
          StringUtils.isNotBlank(countyFile), "Failed to find county file=" + file.getFileName());
      FileToBeProcessed fileToBeProcessed = new FileToBeProcessed(file);
      ListenableFuture<List<Restaurant>> future =
          EXECUTOR_SERVICE.submit(new WebPageProcessAsync(fileToBeProcessed.getCountyName(), file));
      Futures.addCallback(
              future, new WebPageProcessRequestCallBack(countyFile, countDownLatch), EXECUTOR_SERVICE);
    } catch (SecurityException e) {
      log.error("SecurityException caught during async file processing", e);
    }
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
      persistRestaurantData(result);
      countDownLatch.countDown();
    }

    @Override
    public void onFailure(Throwable thrown) {
      log.error("Failure during Future callback for async file processing", thrown);
      countDownLatch.countDown();
    }

    @Transactional
    private synchronized void persistRestaurantData(List<Restaurant> restaurants) {
      restaurantRepository.saveAll(restaurants);
    }
  }
}


