package com.janeullah.healthinspectionrecords.controller;

import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Methods in this restcontroller are invoked by whomever is running the program.
 */
@SuppressWarnings("unused")
@Slf4j
@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {
    private FirebaseInitialization firebaseInitialization;

    @Autowired
    public HealthCheckController(FirebaseInitialization firebaseInitialization) {
        this.firebaseInitialization = firebaseInitialization;
    }

    @GetMapping(value = "/isAlive")
    @ResponseStatus(HttpStatus.OK)
    public String checkIsAlive() {
        return "OK";
    }

    @GetMapping(value = "/testFirebaseConnectivity")
    public ResponseEntity<HttpStatus> testFirebaseConnectivity() {
        if (firebaseInitialization.isDatabaseInitialized()) {
            firebaseInitialization.printCounties();
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
    }
}
