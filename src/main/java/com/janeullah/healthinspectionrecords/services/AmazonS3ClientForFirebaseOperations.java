package com.janeullah.healthinspectionrecords.services;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Slf4j
@Service
public class AmazonS3ClientForFirebaseOperations {

    private AmazonS3 s3client;

    @Value("${NEGA_BUCKET_ACCESS_KEY}")
    private String negaBucketAccessKey;

    @Value("${NEGA_BUCKET_SECRET_KEY}")
    private String negaBucketSecretKey;

    @Value("${NEGA_BUCKET_KEY}")
    private String negaBucketFileNameKey;

    @Value("${NEGA_BUCKET_NAME_READONLY}")
    private String negaReadOnlyBucketName;

    //https://medium.com/oril/uploading-files-to-aws-s3-bucket-using-spring-boot-483fcb6f8646
    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(negaBucketAccessKey, negaBucketSecretKey);
        this.s3client =
                AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withRegion(Regions.US_EAST_1)
                        .build();
    }

    /**
     * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
     * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-project-gradle.html
     *
     * @return InputStream of the item from S3
     */
    public InputStream getFirebaseCredentials() throws SdkClientException {
        S3Object object =
                s3client.getObject(new GetObjectRequest(negaReadOnlyBucketName, negaBucketFileNameKey));
        log.info("Content-Type: {}", object.getObjectMetadata().getContentType());
        return object.getObjectContent();
    }
}