package com.janeullah.healthinspectionrecords.services;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponse;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.util.DataUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-indexing.html
 * Author: jane
 * Date:  9/24/2017
 */
@Service
public class AwsElasticSearchDocumentService extends ElasticSearchDocumentService {
    private static final String AWS_ES_URL = System.getenv("AWS_ES_URL").concat("/restaurants/restaurant/{id}");

    public AwsElasticSearchDocumentService(){}

    public AwsElasticSearchDocumentService(RestClientTemplateBuilder restClientTemplateBuilder){
        super(restClientTemplateBuilder);
    }

    public Response<HttpResponse> addDocumentToAWS(Long id, FlattenedRestaurant flattenedRestaurant){
        //Instantiate the request
        Request<Void> request = new DefaultRequest<>("es"); //Request to ElasticSearch
        request.setHttpMethod(HttpMethodName.PUT);
        request.setEndpoint(URI.create(System.getenv("AWS_ES_URL").concat("/restaurants/restaurant/" + id)));
        request.setContent(IOUtils.toInputStream(DataUtil.writeValueAsString(flattenedRestaurant)));

        //Sign it...
        AWS4Signer signer = new AWS4Signer();
        signer.setRegionName("us-east-1");
        signer.setServiceName(request.getServiceName());
        signer.sign(request, new BasicAWSCredentials(System.getenv("AWS_ES_NEGA_ACCESS_KEY"), System.getenv("AWS_ES_NEGA_SECRET")));

        //Execute it and get the response...
        return new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true))
                .request(request)
                .errorResponseHandler(new SimpleAwsErrorHandler(false))
                .execute(new SimpleAwsResponseHandler(false));
    }

}
