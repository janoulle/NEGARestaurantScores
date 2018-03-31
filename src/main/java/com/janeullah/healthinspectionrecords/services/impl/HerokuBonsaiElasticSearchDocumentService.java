package com.janeullah.healthinspectionrecords.services.impl;

import com.google.common.collect.ImmutableMap;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.rest.RemoteRestClient;
import com.janeullah.healthinspectionrecords.services.ElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.ElasticSearchable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/** Author: jane Date: 10/21/2017 */
@Slf4j
@Service
public class HerokuBonsaiElasticSearchDocumentService extends ElasticSearchDocumentService
    implements ElasticSearchable {
  private static final String HEROKU_BONSAI_URL =
      System.getenv("BONSAI_URL").concat("/restaurants/restaurant/");

  public HerokuBonsaiElasticSearchDocumentService() {}

  @Autowired
  public HerokuBonsaiElasticSearchDocumentService(RemoteRestClient restClient) {
    super(restClient);
  }

  // replace pathvariable with map value
  @Override
  public ResponseEntity<String> addRestaurantDocument(
      Long id, FlattenedRestaurant flattenedRestaurant) {
    HttpEntity<FlattenedRestaurant> requestWithHeaders =
        restClient.getHttpRequestEntityForExchange(flattenedRestaurant, getAuthHeaders());
    return restClient
        .getHttpsRestTemplate()
        .exchange(
            HEROKU_BONSAI_URL + Long.toString(id),
            HttpMethod.POST,
            requestWithHeaders,
            String.class);
    // restTemplate.postForEntity(HEROKU_BONSAI_URL + Long.toString(id), flattenedRestaurant);
  }

  private Map<String, String> getAuthHeaders() {
    try {
      Base64.Encoder encoder = Base64.getEncoder();
      String base64EncodedValue =
          new String(
              encoder.encode(
                  (System.getenv("BONSAI_USERNAME") + ":" + System.getenv("BONSAI_PASSWORD"))
                      .getBytes("UTF-8")));
      return ImmutableMap.of("Authorization", "Basic " + base64EncodedValue);
    } catch (Exception e) {
      log.error("Error generating auth header", e);
    }
    return new HashMap<>();
  }
}
