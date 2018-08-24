package com.janeullah.healthinspectionrecords.external.firebase

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.janeullah.healthinspectionrecords.services.external.aws.AmazonS3ClientForFirebaseOperations
import com.janeullah.healthinspectionrecords.services.external.firebase.FirebaseDataProcessing
import com.janeullah.healthinspectionrecords.services.external.firebase.FirebaseInitialization
import com.janeullah.healthinspectionrecords.util.TestFileUtil
import org.slf4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Field
import java.lang.reflect.Modifier

class FirebaseInitializationSpec extends Specification {

    def firebaseDataProcessing = Mock(FirebaseDataProcessing)
    def amazonS3ClientForFirebaseOperations = Mock(AmazonS3ClientForFirebaseOperations)
    def firebaseInitialization = new FirebaseInitialization(firebaseDataProcessing, amazonS3ClientForFirebaseOperations)

    def setup() {
        firebaseInitialization.negaFirebaseDbUrl = "https://nega-url.com"

        // https://stackoverflow.com/questions/30372186/mocking-slf4j-with-spock
        // https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
        Field classLoggerField = FirebaseInitialization.class.getDeclaredField("log");
        classLoggerField.setAccessible(true);

        Field modifiersField = Field.getDeclaredField("modifiers")
        modifiersField.accessible = true
        modifiersField.setInt(classLoggerField, classLoggerField.getModifiers() & ~Modifier.FINAL)

        classLoggerField.set(null, Mock(Logger));
    }

    @Unroll("Failing to fetch S3 credentials for connecting to Firebase #error")
    def "failed to get S3 credentials"() {
        given:
        amazonS3ClientForFirebaseOperations.getFirebaseCredentials() >> { throw error }
        1 * firebaseInitialization.log.error(_ as String, _ as Exception)

        when:
        firebaseInitialization.connectToFirebaseApp()

        then:
        !firebaseInitialization.isDatabaseInitialized()

        where:
        error                              | _
        new AmazonServiceException("blah") | _
        new AmazonClientException("blah")  | _
        new IOException()                  | _
        new Exception()                    | _

    }

    def "Firebase credential retrieval success"() {
        given:
        def inputStream = TestFileUtil.readFile("src/test/resources/mock-config-file.json")
        amazonS3ClientForFirebaseOperations.getFirebaseCredentials() >> inputStream
        2 * firebaseInitialization.log.info(_ as String)

        when:
        firebaseInitialization.connectToFirebaseApp()

        then:
        firebaseInitialization.isDatabaseInitialized()
    }

}