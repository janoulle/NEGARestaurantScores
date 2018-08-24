package com.janeullah.healthinspectionrecords.external.firebase;

import com.google.firebase.database.DatabaseReference;
import com.janeullah.healthinspectionrecords.services.external.aws.AmazonS3ClientForFirebaseOperations;
import com.janeullah.healthinspectionrecords.services.external.firebase.FirebaseDataProcessing;
import com.janeullah.healthinspectionrecords.services.external.firebase.FirebaseInitialization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseInitializationTest {

    @InjectMocks
    private FirebaseInitialization firebaseInitialization;

    @Mock
    private FirebaseDataProcessing firebaseDataProcessing;

    @Mock
    private AmazonS3ClientForFirebaseOperations amazonS3ClientForFirebaseOperations;

    private DatabaseReference dbRef = mock(DatabaseReference.class);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(firebaseInitialization, "negaFirebaseDbUrl", "https://dbxyi.io");
        ReflectionTestUtils.setField(firebaseInitialization, "database", dbRef);
        when(dbRef.child("counties")).thenReturn(mock(DatabaseReference.class));
        when(dbRef.child("restaurants")).thenReturn(mock(DatabaseReference.class));
        when(dbRef.child("violations")).thenReturn(mock(DatabaseReference.class));

        // InputStream is =
        // FirebaseInitializationTest.class.getResourceAsStream("mock-config-file.json");
        // when(amazonS3ClientForFirebaseOperations.getFirebaseCredentials()).thenReturn(is);
    }

    @Test
    public void testReadRecordsFromLocalAndWriteToRemote() {
        when(firebaseDataProcessing.createAndRetrieveMapOfCounties(anyMap()))
                .thenReturn(new HashMap<>());
        when(firebaseDataProcessing.createAndRetrieveViolations(anyMap())).thenReturn(new HashMap<>());
        when(firebaseDataProcessing.flattenMapOfRestaurants(anyMap())).thenReturn(new HashMap<>());

        assertTrue(firebaseInitialization.readRecordsFromLocalAndWriteToRemote());
    }
}
