package com.janeullah.healthinspectionrecords.events;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.janeullah.healthinspectionrecords.async.WebPageProcessAsync;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.util.ExecutorUtil;
import com.janeullah.healthinspectionrecords.util.FilesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static com.janeullah.healthinspectionrecords.util.ExecutorUtil.executorService;

/**
 * Author: Jane Ullah
 * Date:  9/17/2016
 */
@Slf4j
@Component
public class WebPageProcessing {
    private static CountDownLatch doneSignal = new CountDownLatch(ExecutorUtil.getThreadCount());
    private RestaurantRepository restaurantRepository;

    @Autowired
    public WebPageProcessing(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public void startProcessingOfDownloadedFiles() {
        try {
            File[] files = FilesUtil.getFilesInDirectory(WebPageConstants.PATH_TO_PAGE_STORAGE);
            submitAsyncProcessingRequests(files);
            //TODO: figure out way to wait for all execution to be complete
        } catch (Exception e) {
            log.error("Exception in startProcessingOfDownloadedFiles", e);
        }
    }

    private void submitAsyncProcessingRequests(File[] files) {
        Stream.of(files).forEach(file -> asyncProcessFile(file.toPath()));
    }

    public Optional<ListenableFuture<List<Restaurant>>> asyncProcessFile(Path file) {
        try {
            String countyFile = FilenameUtils.getName(file.getFileName().toString());
            Preconditions.checkArgument(StringUtils.isNotBlank(countyFile), "Failed to find county file=" + file.getFileName());
            ListenableFuture<List<Restaurant>> future = executorService.submit(new WebPageProcessAsync(FilesUtil.extractCounty(file), file, doneSignal));
            registerCallbackForFuture(countyFile, future);
            return Optional.of(future);
        } catch (SecurityException e) {
            log.error("SecurityException caught during async file processing", e);
        }
        return Optional.empty();
    }

    private void registerCallbackForFuture(String countyFile, ListenableFuture<List<Restaurant>> future) {
        Futures.addCallback(future, new FutureCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> result) {
                log.info("Web Page Processing completed for county: {} size: {}", countyFile, result.size());
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
        restaurantRepository.save(restaurants);
    }
}
