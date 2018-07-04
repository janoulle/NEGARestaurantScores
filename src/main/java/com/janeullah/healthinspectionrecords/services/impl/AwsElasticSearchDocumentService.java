package com.janeullah.healthinspectionrecords.services.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpResponse;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.external.aws.AwsV4RequestSigner;
import com.janeullah.healthinspectionrecords.external.aws.SimpleAwsErrorHandler;
import com.janeullah.healthinspectionrecords.external.aws.SimpleAwsResponseHandler;
import com.janeullah.healthinspectionrecords.services.ElasticSearchable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/** https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-indexing.html
 * http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html */
@Slf4j
@Service
public class AwsElasticSearchDocumentService implements ElasticSearchable<HttpStatus> {

  @Value("${AWS_ES_SERVICE_NAME}")
  private String awsElasticSearchServiceName;

  @Value("${AWS_ES_REGION_NAME}")
  private String awsRegionName;

  @Value("${AWS_ES_NEGA_ACCESS_KEY}")
  private String awsElasticSearchAccessKey;

  @Value("${AWS_ES_NEGA_SECRET}")
  private String awsElasticSearchSecret;

  @Value("${AWS_ES_URL}")
  private String awsElasticSearchUrl;

  @Override
  public ResponseEntity<HttpStatus> addRestaurantDocument(
      Long id, FlattenedRestaurant flattenedRestaurant) {
    AwsV4RequestSigner awsV4RequestSigner =
        new AwsV4RequestSigner(
            new BasicAWSCredentials(awsElasticSearchAccessKey, awsElasticSearchSecret),
            awsRegionName,
            awsElasticSearchServiceName);
    // Instantiate the request
    Request<Void> request =
        awsV4RequestSigner.makeSignableRequest(
            flattenedRestaurant, awsElasticSearchUrl.concat("/restaurants/restaurant/" + id));
    request = awsV4RequestSigner.signRequest(request);

    // Execute it and get the response...
    Response<HttpResponse> response =
        new AmazonHttpClient(new ClientConfiguration())
            .requestExecutionBuilder()
            .executionContext(new ExecutionContext(true))
            .request(request)
            .errorResponseHandler(new SimpleAwsErrorHandler(false))
            .execute(new SimpleAwsResponseHandler(false));
    if (response.getHttpResponse().getStatusCode() < 200
        || response.getHttpResponse().getStatusCode() >= 300) {
      log.error(
          "awsResponse={} httpResponse={} statusCode={}",
          response.getAwsResponse(),
          response.getHttpResponse(),
          response.getHttpResponse().getStatusCode());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
