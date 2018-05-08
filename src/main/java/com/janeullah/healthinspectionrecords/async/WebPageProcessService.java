package com.janeullah.healthinspectionrecords.async;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.util.FilesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
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

  public Optional<ListenableFuture<List<Restaurant>>> submitFileForProcessing(
      Path file, CountDownLatch countDownLatch) {
    try {
      String countyFile = FilenameUtils.getName(file.getFileName().toString());
      Preconditions.checkArgument(
          StringUtils.isNotBlank(countyFile), "Failed to find county file=" + file.getFileName());
      ListenableFuture<List<Restaurant>> future =
          EXECUTOR_SERVICE.submit(
              new WebPageProcessAsync(FilesUtil.extractCounty(file), file, countDownLatch));
      registerCallbackForFuture(countyFile, future);
      return Optional.of(future);
    } catch (SecurityException e) {
      log.error("SecurityException caught during async file processing", e);
    }
    return Optional.empty();
  }

  private void registerCallbackForFuture(
      String countyFile, ListenableFuture<List<Restaurant>> future) {
    Futures.addCallback(
        future,
        new FutureCallback<List<Restaurant>>() {
          @Override
          public void onSuccess(List<Restaurant> result) {
            log.info(
                "Web Page Processing completed for county: {} size: {}", countyFile, result.size());
            persistRestaurantData(result);
          }

          @Override
          public void onFailure(Throwable thrown) {
            log.error("Failure during Future callback for async file processing", thrown);
          }
        });
  }

  @Transactional
  private synchronized void persistRestaurantData(List<Restaurant> restaurants) {
    restaurantRepository.saveAll(restaurants);
  }
}
