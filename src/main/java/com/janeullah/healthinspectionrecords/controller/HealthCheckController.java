package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {
  private FirebaseInitialization firebaseInitialization;

  @Autowired
  public HealthCheckController(FirebaseInitialization firebaseInitialization) {
    this.firebaseInitialization = firebaseInitialization;
  }

  @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public String checkIsAlive() {
    return "OK";
  }

  @RequestMapping(value = "/testFirebaseConnectivity", method = RequestMethod.GET)
  public ResponseEntity<HttpStatus> testFirebaseConnectivity() {
    if (firebaseInitialization.isDatabaseInitialized()) {
      firebaseInitialization.printCounties();
      return new ResponseEntity<>(HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
  }
}
