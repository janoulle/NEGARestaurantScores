package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.events.ScheduledWebEvents;
import com.janeullah.healthinspectionrecords.events.WebEventOrchestrator;
import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.services.impl.HerokuBonsaiElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.impl.LocalhostElasticSearchDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * https://github.com/janoulle/NEGARestaurantScores/issues/13
 * Methods in this restcontroller are invoked by whomever is running the program.
 * Author: Jane Ullah Date: 3/28/2017
 */
@SuppressWarnings("unused")
@Slf4j
@RequestMapping("/admin")
@RestController
public class MainController {
    private WebEventOrchestrator webEventOrchestrator;
    private FirebaseInitialization firebaseInitialization;
    private LocalhostElasticSearchDocumentService localhostElasticSearchDocumentService;
    private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;
    private RestaurantRepository restaurantRepository;
    private ScheduledWebEvents scheduledWebEvents;

    @Autowired
    public MainController(
            WebEventOrchestrator webEventOrchestrator,
            FirebaseInitialization firebaseInitialization,
            RestaurantRepository restaurantRepository,
            LocalhostElasticSearchDocumentService localhostElasticSearchDocumentService,
            HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService,
            ScheduledWebEvents scheduledWebEvents) {
        this.webEventOrchestrator = webEventOrchestrator;
        this.firebaseInitialization = firebaseInitialization;
        this.restaurantRepository = restaurantRepository;
        this.localhostElasticSearchDocumentService = localhostElasticSearchDocumentService;
        this.herokuBonsaiElasticSearchDocumentService = herokuBonsaiElasticSearchDocumentService;
        this.scheduledWebEvents = scheduledWebEvents;
    }

    @PutMapping(value = "/initializeLocalDB")
    @ResponseStatus(HttpStatus.OK)
    public void writeRecordsToDB() {
        webEventOrchestrator.processAndSaveAllRestaurants();
        log.info("Processing initiated");
    }

    @PutMapping(value = "/initializeFirebaseDB")
    public ResponseEntity<HttpStatus> writeRecordsToFirebase() {
        return firebaseInitialization.readRecordsFromLocalAndWriteToRemote()
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
    }

    @PostMapping(value = "/seedElasticSearchDBLocal")
    public ResponseEntity<HttpStatus> seedElasticSearchDBLocal() {
        List<FlattenedRestaurant> flattenedRestaurants =
                restaurantRepository.findAllFlattenedRestaurants();
        ResponseEntity<String> result =
                localhostElasticSearchDocumentService.addRestaurantDocuments(flattenedRestaurants);
        return new ResponseEntity<>(result.getStatusCode());
    }

    @PostMapping(value = "/seedElasticSearchDBHeroku")
    public ResponseEntity<HttpStatus> seedElasticSearchDBHeroku() {
        return herokuBonsaiElasticSearchDocumentService.handleProcessingOfData()
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/runAll")
    public ResponseEntity<HttpStatus> runAll() {
        return scheduledWebEvents.runScheduledUpdates()
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

}
