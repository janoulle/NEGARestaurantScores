package com.janeullah.healthinspectionrecords.services.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpResponse;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.external.aws.SimpleAwsErrorHandler;
import com.janeullah.healthinspectionrecords.external.aws.SimpleAwsResponseHandler;
import com.janeullah.healthinspectionrecords.rest.RemoteRestClient;
import com.janeullah.healthinspectionrecords.services.ElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.ElasticSearchable;
import com.janeullah.healthinspectionrecords.util.AwsV4RequestSigner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-indexing.html
 * Author: Jane Ullah
 * Date:  9/24/2017
 */
@Slf4j
@Service
public class AwsElasticSearchDocumentService extends ElasticSearchDocumentService implements ElasticSearchable {
    private static final String AWS_ES_SERVICE_NAME = "es";
    private static final String AWS_REGION_NAME = "us-east-1";

    public AwsElasticSearchDocumentService() {
    }

    @Autowired
    public AwsElasticSearchDocumentService(RemoteRestClient restClient) {
        super(restClient);
    }

    @Override
    public ResponseEntity<HttpStatus> addRestaurantDocument(Long id, FlattenedRestaurant flattenedRestaurant) {
        AwsV4RequestSigner awsV4RequestSigner = new AwsV4RequestSigner(new BasicAWSCredentials(System.getenv("AWS_ES_NEGA_ACCESS_KEY"), System.getenv("AWS_ES_NEGA_SECRET")), AWS_REGION_NAME, AWS_ES_SERVICE_NAME);
        //Instantiate the request
        Request<Void> request = awsV4RequestSigner.makeSignableRequest(flattenedRestaurant, System.getenv("AWS_ES_URL").concat("/restaurants/restaurant/" + id));
        request = awsV4RequestSigner.signRequest(request);

        //Execute it and get the response...
        Response<HttpResponse> response = new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true))
                .request(request)
                .errorResponseHandler(new SimpleAwsErrorHandler(false))
                .execute(new SimpleAwsResponseHandler(false));
        if (response.getHttpResponse().getStatusCode() < 200 || response.getHttpResponse().getStatusCode() >= 300) {
            new ResponseEntity<>(HttpStatus.OK);
        }
        log.error("awsResponse={} httpResponse={} statusCode={}", response.getAwsResponse(), response.getHttpResponse(), response.getHttpResponse().getStatusCode());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
