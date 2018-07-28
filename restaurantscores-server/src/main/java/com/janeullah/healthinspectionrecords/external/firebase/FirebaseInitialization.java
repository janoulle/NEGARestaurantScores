package com.janeullah.healthinspectionrecords.external.firebase;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.*;
import com.janeullah.healthinspectionrecords.constants.FirebaseNodeNames;
import com.janeullah.healthinspectionrecords.domain.dtos.County;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedInspectionReport;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.external.aws.AmazonS3ClientForFirebaseOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Author: jane Date: 4/14/2017
 * https://github.com/firebase/quickstart-java/blob/master/database/src/main/java/com/google/firebase/quickstart/Database.java
 */
@Slf4j
@Service
public class FirebaseInitialization {
    private DatabaseReference database;
    private FirebaseDataProcessing firebaseDataProcessing;
    private AmazonS3ClientForFirebaseOperations amazonS3ClientForFirebaseOperations;

    @Value("${NEGA_FIREBASE_DB}")
    private String negaFirebaseDbUrl;

    @Autowired
    public FirebaseInitialization(
            FirebaseDataProcessing firebaseDataProcessing,
            AmazonS3ClientForFirebaseOperations amazonS3ClientForFirebaseOperations) {
        this.firebaseDataProcessing = firebaseDataProcessing;
        this.amazonS3ClientForFirebaseOperations = amazonS3ClientForFirebaseOperations;
    }

    /**
     * This is used by Spring to initialized the database reference for firebase
     */
    @SuppressWarnings("unused")
    @PostConstruct
    private void connectToFirebaseApp() {
        try (InputStream is = amazonS3ClientForFirebaseOperations.getFirebaseCredentials()) {
            FirebaseOptions options = getFirebaseOptions(is);
            FirebaseApp.initializeApp(options);
            log.info("Firebase app initialized");
            database = FirebaseDatabase.getInstance().getReference("nega");
            log.info("Firebase db initialized");
        } catch (AmazonServiceException ase) {
            String sb =
                    ("Caught an AmazonServiceException, which means your request made it "
                            + "to Amazon S3, but was rejected with an error response for some reason.")
                            + "Error Message:    "
                            + ase.getMessage()
                            + "HTTP Status Code: "
                            + ase.getStatusCode()
                            + "AWS Error Code:   "
                            + ase.getErrorCode()
                            + "Error Type:       "
                            + ase.getErrorType()
                            + "Request ID:       "
                            + ase.getRequestId();
            log.error(sb, ase);
        } catch (AmazonClientException ace) {
            String sb =
                    ("Caught an AmazonClientException, which means the client encountered "
                            + "a serious internal problem while trying to communicate with S3, "
                            + "such as not being able to access the network.")
                            + "Error Message: "
                            + ace.getMessage();
            log.error(sb, ace);
        } catch (IOException e) {
            log.error("IO Exception thrown while fetching credentials", e);
        } catch (DatabaseException de) {
            log.error("Firebase app initialized but DB not initialized", de);
        } catch (Exception e) {
            log.error("Unable to connect to firebase app with provided credential file", e);
        }
    }

    private FirebaseOptions getFirebaseOptions(InputStream serviceAccount) {
        return new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl(negaFirebaseDbUrl)
                .build();
    }

    public boolean isDatabaseInitialized() {
        return getDatabase().isPresent();
    }

    private Optional<DatabaseReference> getDatabase() {
        return Optional.ofNullable(database);
    }

    public boolean readRecordsFromLocalAndWriteToRemote() {
        try {
            Map<String, List<Restaurant>> mapOfCountiesToRestaurants = saveFlattenedCounties();
            Map<String, FlattenedRestaurant> restaurantData =
                    saveFlattenedRestaurants(mapOfCountiesToRestaurants);
            saveFlattenedViolations(restaurantData);
            return true;
        } catch (Exception e) {
            log.error("Exception encountered during readRecordsFromLocalAndWriteToRemote", e);
        }
        return false;
    }

    private void saveFlattenedViolations(Map<String, FlattenedRestaurant> restaurantData) {
        DatabaseReference violationsRef = database.child(FirebaseNodeNames.VIOLATIONS.getNodeName());
        Map<String, FlattenedInspectionReport> violationsData =
                firebaseDataProcessing.createAndRetrieveViolations(restaurantData);
        DatabaseReference.CompletionListener listener =
                getCompletionListener(FirebaseNodeNames.VIOLATIONS.getNodeName(), violationsData);
        violationsRef.setValue(violationsData, listener);
    }

    private Map<String, List<Restaurant>> saveFlattenedCounties() {
        DatabaseReference countiesRef = database.child(FirebaseNodeNames.COUNTIES.getNodeName());
        Map<String, List<Restaurant>> mapOfCountiesToRestaurants = new HashMap<>();
        Map<String, County> countiesAndRestaurants =
                firebaseDataProcessing.createAndRetrieveMapOfCounties(mapOfCountiesToRestaurants);
        DatabaseReference.CompletionListener listener =
                getCompletionListener(FirebaseNodeNames.COUNTIES.getNodeName(), countiesAndRestaurants);
        countiesRef.setValue(countiesAndRestaurants, listener);
        return mapOfCountiesToRestaurants;
    }

    private Map<String, FlattenedRestaurant> saveFlattenedRestaurants(
            Map<String, List<Restaurant>> mapOfCountiesToRestaurants) {
        DatabaseReference restaurantsRef = database.child(FirebaseNodeNames.RESTAURANTS.getNodeName());
        Map<String, FlattenedRestaurant> restaurantData =
                firebaseDataProcessing.flattenMapOfRestaurants(mapOfCountiesToRestaurants);
        DatabaseReference.CompletionListener listener =
                getCompletionListener(FirebaseNodeNames.RESTAURANTS.getNodeName(), restaurantData);
        restaurantsRef.setValue(restaurantData, listener);
        return restaurantData;
    }

    private DatabaseReference.CompletionListener getCompletionListener(String desc, Map data) {
        return (databaseError, databaseReference) -> {
            if (databaseError != null) {
                log.error("Data  for child ({}) could not be saved - {}", desc, databaseError.getMessage());
            } else {
                log.info("Data for child ({} of size {}) saved successfully.", desc, data.size());
            }
        };
    }

    public void printCounties() {
        getDatabase()
                .ifPresent(
                        databaseReference -> {
                            final DatabaseReference negaChildDBReference = databaseReference.child("counties");
                            negaChildDBReference
                                    .orderByKey()
                                    .addChildEventListener(
                                            new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                                    County county = dataSnapshot.getValue(County.class);
                                                    log.debug(
                                                            "County: {}, County Restaurant Size: {}, Key: {}, Snapshot size: {}",
                                                            county.getName(),
                                                            county.getRestaurants().size(),
                                                            dataSnapshot.getKey(),
                                                            dataSnapshot.getChildrenCount());
                                                }

                                                @Override
                                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                    /*
                                                     *
                                                     * To be completed at a later date
                                                     */
                                                }

                                                @Override
                                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                    /*
                                                     *
                                                     * To be completed at a later date
                                                     */
                                                }

                                                @Override
                                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                                    /*
                                                     *
                                                     * To be completed at a later date
                                                     */
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    /*
                                                     *
                                                     * To be completed at a later date
                                                     */
                                                }
                                            });
                        });
    }
}
