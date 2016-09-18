package com.janeullah.healthinspectionrecords.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.janeullah.healthinspectionrecords.async.WebPageProcessAsync;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.util.ExecutorUtil;
import com.janeullah.healthinspectionrecords.util.FilesUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static com.janeullah.healthinspectionrecords.util.ExecutorUtil.executorService;

/**
 * Author: Jane Ullah
 * Date:  9/17/2016
 */
@Component
public class WebPageProcessing {
    private static final Logger logger = LoggerFactory.getLogger(WebPageProcessing.class);
    private static final Map<String, List<Restaurant>> restaurantsByCounties = new ConcurrentHashMap<>();
    private static CountDownLatch doneSignal = new CountDownLatch(ExecutorUtil.getThreadCount());

    @Autowired
    private RestaurantRepository restaurantRepository;

    public void startProcessingOfDownloadedFiles() {
        try {
            File[] files = FilesUtil.getFilesInDirectory(WebPageConstants.PATH_TO_PAGE_STORAGE);
            List<ListenableFuture<List<Restaurant>>> futures = submitAsyncProcessingRequests(files);
            //TODO: figure out way to wait for all execution to be complete
        } catch (Exception e) {
            logger.error("Exception in startProcessingOfDownloadedFiles", e);
        }
    }

    private List<ListenableFuture<List<Restaurant>>> submitAsyncProcessingRequests(File[] files){
        List<ListenableFuture<List<Restaurant>>> futures = new ArrayList<>();
        Stream.of(files).forEach(file -> {
            Optional<ListenableFuture<List<Restaurant>>> future = asyncProcessFile(file.toPath());
            future.ifPresent(futures::add);
        });
        return futures;
    }

    /**
     * @param file Path to downloaded file relative
     */
    public Optional<ListenableFuture<List<Restaurant>>> asyncProcessFile(Path file) {
        try {
            String countyFile = FilenameUtils.getName(file.getFileName().toString());
            ListenableFuture<List<Restaurant>> future = executorService.submit(new WebPageProcessAsync(FilesUtil.extractCounty(file), file, doneSignal));
            Futures.addCallback(future, new FutureCallback<List<Restaurant>>() {
                @Override
                public void onSuccess(List<Restaurant> result) {
                    System.out.format("Web Page Processing completed for county: %s size: %d\n", countyFile, result.size());
                    restaurantsByCounties.put(countyFile, result);
                    persistRestaurantData(result);
                }

                @Override
                public void onFailure(Throwable thrown) {
                    logger.error("Failure during Future callback for async file processing",thrown);
                }
            });
            return Optional.of(future);
        } catch (SecurityException e) {
            logger.error("SecurityException caught during async file processing", e);
        }
        return Optional.empty();
    }

    @Transactional
    private synchronized void persistRestaurantData(List<Restaurant> restaurants) {
        restaurantRepository.save(restaurants);
    }
}
