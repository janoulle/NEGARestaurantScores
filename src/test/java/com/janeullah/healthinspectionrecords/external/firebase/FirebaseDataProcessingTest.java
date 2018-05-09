package com.janeullah.healthinspectionrecords.external.firebase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseDataProcessingTest {

    @InjectMocks
    private FirebaseDataProcessing firebaseDataProcessing;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateAndRetrieveMapOfCounties() {
        assertTrue(true);
    }
}
