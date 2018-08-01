package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.services.impl.HerokuBonsaiElasticSearchDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class ScheduledWebEvents {
    // private static final String EVERY_SATURDAY = "0 0 8 1/14 * sat";
    private FirebaseInitialization firebaseInitialization;
    private WebEventOrchestrator webEventOrchestrator;
    private RestaurantRepository restaurantRepository;
    private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;

    @Autowired
    public ScheduledWebEvents(FirebaseInitialization firebaseInitialization,
                              WebEventOrchestrator webEventOrchestrator,
                              RestaurantRepository restaurantRepository,
                              HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService) {
        this.firebaseInitialization = firebaseInitialization;
        this.webEventOrchestrator = webEventOrchestrator;
        this.restaurantRepository = restaurantRepository;
        this.herokuBonsaiElasticSearchDocumentService = herokuBonsaiElasticSearchDocumentService;
    }

    // https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
    // @Scheduled(cron = EVERY_SATURDAY)
    public boolean runAllUpdates() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        log.info("Starting scheduled task. The time is now {}", dateFormat.format(new Date()));

        // todo: use aspect to track method execution time
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //clear records in case the application wasn't restarted. On every app restart, the db is cleared
        restaurantRepository.deleteAll();

        // step required after deleting records since this processes any found files or redownloads the html files
        webEventOrchestrator.processAndSaveAllRestaurants();
        boolean firebaseSave = false;
        boolean herokuSave = false;

        long restaurantCount = restaurantRepository.count();
        if (restaurantCount > 0L) {

            firebaseSave = firebaseInitialization.readRecordsFromLocalAndWriteToRemote();
            log.info("firebaseSave={}", firebaseSave);

            herokuSave = herokuBonsaiElasticSearchDocumentService.handleProcessingOfData();
            log.info("herokuSave={}", herokuSave);
        }

        stopWatch.stop();

        log.info("timeElapsed={} restaurantCount={}", stopWatch.getTotalTimeMillis(), restaurantCount);
        return firebaseSave && herokuSave;
    }

}
