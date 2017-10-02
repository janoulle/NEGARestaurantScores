package com.janeullah.healthinspectionrecords.services;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpResponse;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.util.AwsV4RequestSigner;
import org.springframework.stereotype.Service;

/**
 * https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-indexing.html
 * Author: jane
 * Date:  9/24/2017
 */
@Service
public class AwsElasticSearchDocumentService extends ElasticSearchDocumentService {
    private static final String AWS_ES_SERVICE_NAME = "es";
    private static final String AWS_REGION_NAME = "us-east-1";
    private static final String AWS_ES_URL = System.getenv("AWS_ES_URL").concat("/restaurants/restaurant/{id}");

    public AwsElasticSearchDocumentService(){}

    public AwsElasticSearchDocumentService(RestClientTemplateBuilder restClientTemplateBuilder){
        super(restClientTemplateBuilder);
    }

    public Response<HttpResponse> addDocumentToAWS(Long id, FlattenedRestaurant flattenedRestaurant){
        AwsV4RequestSigner awsV4RequestSigner = new AwsV4RequestSigner(new BasicAWSCredentials(System.getenv("AWS_ES_NEGA_ACCESS_KEY"), System.getenv("AWS_ES_NEGA_SECRET")),AWS_REGION_NAME, AWS_ES_SERVICE_NAME);
        //Instantiate the request
        Request<Void> request = awsV4RequestSigner.makeSignableRequest(flattenedRestaurant,System.getenv("AWS_ES_URL").concat("/restaurants/restaurant/" + id));
        request = awsV4RequestSigner.signRequest(request);

        //Execute it and get the response...
        return new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true))
                .request(request)
                .errorResponseHandler(new SimpleAwsErrorHandler(false))
                .execute(new SimpleAwsResponseHandler(false));
    }

}
