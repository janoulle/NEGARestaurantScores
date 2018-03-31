package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.events.WebEventOrchestrator;
import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization;
import com.janeullah.healthinspectionrecords.services.impl.AwsElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.impl.HerokuBonsaiElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.impl.LocalhostElasticSearchDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** TODO: add auth for this controller class Author: Jane Ullah Date: 3/28/2017 */
@Slf4j
@RequestMapping("/admin")
@RestController
public class MainController {
  private WebEventOrchestrator webEventOrchestrator;
  private FirebaseInitialization firebaseInitialization;
  private LocalhostElasticSearchDocumentService localhostElasticSearchDocumentService;
  private HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService;
  private AwsElasticSearchDocumentService awsElasticSearchDocumentService;
  private RestaurantController restaurantController;
  private ViolationsController violationsController;

  @Autowired
  public MainController(
      WebEventOrchestrator webEventOrchestrator,
      FirebaseInitialization firebaseInitialization,
      RestaurantController restaurantController,
      ViolationsController violationsController,
      AwsElasticSearchDocumentService awsElasticSearchDocumentService,
      LocalhostElasticSearchDocumentService localhostElasticSearchDocumentService,
      HerokuBonsaiElasticSearchDocumentService herokuBonsaiElasticSearchDocumentService) {
    this.webEventOrchestrator = webEventOrchestrator;
    this.firebaseInitialization = firebaseInitialization;
    this.restaurantController = restaurantController;
    this.awsElasticSearchDocumentService = awsElasticSearchDocumentService;
    this.violationsController = violationsController;
    this.localhostElasticSearchDocumentService = localhostElasticSearchDocumentService;
    this.herokuBonsaiElasticSearchDocumentService = herokuBonsaiElasticSearchDocumentService;
  }

  @RequestMapping(value = "/initializeLocalDB", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  public void writeRecordsToDB() {
    webEventOrchestrator.processAndSaveAllRestaurants();
    log.info("Processing initiated");
  }

  @RequestMapping(value = "/testFirebaseConnectivity", method = RequestMethod.GET)
  public ResponseEntity<HttpStatus> testFirebaseConnectivity() {
    return firebaseInitialization.isDatabaseInitialized()
        ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
  }

  @RequestMapping(value = "/initializeFirebaseDB", method = RequestMethod.PUT)
  public ResponseEntity<HttpStatus> writeRecordsToFirebase() {
    return firebaseInitialization.readRecordsFromLocalAndWriteToRemote()
        ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
  }

  @RequestMapping(value = "/seedElasticSearchDBLocal", method = RequestMethod.POST)
  public ResponseEntity<HttpStatus> seedElasticSearchDBLocal() {
    Iterable<FlattenedRestaurant> flattenedRestaurants = restaurantController.fetchAllFlattened();
    for (FlattenedRestaurant flattenedRestaurant : flattenedRestaurants) {
      updateViolationInformation(flattenedRestaurant);
      ResponseEntity<String> status =
          localhostElasticSearchDocumentService.addRestaurantDocument(
              flattenedRestaurant.getId(), flattenedRestaurant);
      if (!status.getStatusCode().is2xxSuccessful()) {
        log.error(
            "Failed to write data about restaurant={} to the db with response={}",
            flattenedRestaurant,
            status.getBody());
      }
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // todo: update hibernate query to fetch this info
  private void updateViolationInformation(FlattenedRestaurant restaurant) {
    List<Violation> allViolations =
        violationsController.findViolationsByRestaurantId(restaurant.getId());
    Map<Severity, Long> mapOfSeverityToViolations =
        allViolations
            .stream()
            .collect(Collectors.groupingBy(Violation::getSeverity, Collectors.counting()));
    restaurant.setCriticalViolations(
        MapUtils.getInteger(mapOfSeverityToViolations, Severity.CRITICAL, 0));
    restaurant.setNonCriticalViolations(
        MapUtils.getInteger(mapOfSeverityToViolations, Severity.NONCRITICAL, 0));
  }

  @RequestMapping(value = "/seedElasticSearchDBAWS", method = RequestMethod.POST)
  public ResponseEntity<HttpStatus> seedElasticSearchDBAWS() {
    Iterable<FlattenedRestaurant> flattenedRestaurants = restaurantController.fetchAllFlattened();
    for (FlattenedRestaurant flattenedRestaurant : flattenedRestaurants) {
      updateViolationInformation(flattenedRestaurant);
      ResponseEntity<HttpStatus> resp =
          awsElasticSearchDocumentService.addRestaurantDocument(
              flattenedRestaurant.getId(), flattenedRestaurant);
      if (!resp.getStatusCode().is2xxSuccessful()) {
        log.error("Failed to write data about restaurant={} to the db", flattenedRestaurant);
      }
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // todo: consolidate into single method with clean method of swapping implementation server (aws,
  // heroku, local)
  @RequestMapping(value = "/seedElasticSearchDBHeroku", method = RequestMethod.POST)
  public ResponseEntity<HttpStatus> seedElasticSearchDBHeroku() {
    Iterable<FlattenedRestaurant> flattenedRestaurants = restaurantController.fetchAllFlattened();
    for (FlattenedRestaurant flattenedRestaurant : flattenedRestaurants) {
      updateViolationInformation(flattenedRestaurant);
      ResponseEntity<String> resp =
          herokuBonsaiElasticSearchDocumentService.addRestaurantDocument(
              flattenedRestaurant.getId(), flattenedRestaurant);
      if (!resp.getStatusCode().is2xxSuccessful()) {
        log.error(
            "Failed to write data about restaurant={} to the db with response={}",
            flattenedRestaurant,
            resp);
      }
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/printCountiesFromFirebase", method = RequestMethod.GET)
  public void printCountiesFromFirebase() {
    firebaseInitialization.printCounties();
  }
}
