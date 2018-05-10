package com.janeullah.healthinspectionrecords.external.firebase;

import com.janeullah.healthinspectionrecords.external.aws.AmazonS3ClientForFirebaseOperations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseInitializationNotReady {

    @InjectMocks
    private FirebaseInitialization firebaseInitialization;

    @Mock
    private FirebaseDataProcessing firebaseDataProcessing;

    @Mock
    private AmazonS3ClientForFirebaseOperations amazonS3ClientForFirebaseOperations;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        /*ReflectionTestUtils.setField(firebaseInitialization, "negaFirebaseDbUrl", "https://dbxyi.io");

        InputStream is = FirebaseInitializationNotReady.class.getResourceAsStream("mock-config-file.json");
        when(amazonS3ClientForFirebaseOperations.getFirebaseCredentials()).thenReturn(is);*/
    }

    @Test
    public void testTrue() {
        assertTrue(true);
    }

//    @Ignore("Figure out best way to test this")
//    @Test
//    public void testReadRecordsFromLocalAndWriteToRemote() {
//        when(firebaseDataProcessing.createAndRetrieveViolations(anyMap())).thenReturn(new HashMap<>());
//        when(firebaseDataProcessing.createAndRetrieveMapOfCounties(anyMap())).thenReturn(new HashMap<>());
//        when(firebaseDataProcessing.flattenMapOfRestaurants(anyMap())).thenReturn(new HashMap<>());
//
//        assertTrue(firebaseInitialization.readRecordsFromLocalAndWriteToRemote());
//
//    }
}
