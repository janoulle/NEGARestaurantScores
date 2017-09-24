package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.external.FirebaseInitialization;
import com.janeullah.healthinspectionrecords.services.ElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.WebEventOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: add auth for this controller class
 * Author: Jane Ullah
 * Date:  3/28/2017
 */
@RequestMapping("/admin")
@RestController
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private WebEventOrchestrator webEventOrchestrator;
    private FirebaseInitialization firebaseInitialization;
    private ElasticSearchDocumentService elasticSearchDocumentService;
    private RestaurantController restaurantController;

    @Autowired
    public MainController(WebEventOrchestrator webEventOrchestrator, FirebaseInitialization firebaseInitialization, ElasticSearchDocumentService elasticSearchDocumentService, RestaurantController restaurantController){
        this.webEventOrchestrator = webEventOrchestrator;
        this.firebaseInitialization = firebaseInitialization;
        this.elasticSearchDocumentService = elasticSearchDocumentService;
        this.restaurantController = restaurantController;
    }

    @RequestMapping(value = "/initializeLocalDB", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void writeRecordsToDB() {
        webEventOrchestrator.processAndSaveAllRestaurants();
        logger.info("Processing initiated");
    }

    @RequestMapping(value = "/testFirebaseConnectivity", method = RequestMethod.GET)
    public ResponseEntity<HttpStatus> testFirebaseConnectivity(){
        return firebaseInitialization.isDatabaseInitialized()
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
    }

    @RequestMapping(value = "/initializeFirebaseDB", method = RequestMethod.PUT)
    public ResponseEntity<HttpStatus> writeRecordsToFirebase(){
        return firebaseInitialization.readRecordsFromLocalAndWriteToRemote()
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
    }

    @RequestMapping(value = "/seedElasticSearchDB", method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> seedElasticSearchDB(){
        Iterable<FlattenedRestaurant> flattenedRestaurants = restaurantController.fetchAllFlattened();
        for(FlattenedRestaurant flattenedRestaurant : flattenedRestaurants){
            ResponseEntity<String> status = elasticSearchDocumentService.addDocument(flattenedRestaurant.getId(),flattenedRestaurant);
            if (!status.getStatusCode().is2xxSuccessful()){
                logger.error("Failed to write data about restaurant={} to the db with response={}",flattenedRestaurant,status.getBody());
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/printCountiesFromFirebase", method = RequestMethod.GET)
    public void printCountiesFromFirebase(){
        firebaseInitialization.printCounties();
    }
}
