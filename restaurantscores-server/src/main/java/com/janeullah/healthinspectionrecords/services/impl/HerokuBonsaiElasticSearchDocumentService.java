package com.janeullah.healthinspectionrecords.services.impl;

import com.google.common.collect.ImmutableMap;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.rest.RemoteRestClient;
import com.janeullah.healthinspectionrecords.services.ElasticSearchable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class HerokuBonsaiElasticSearchDocumentService implements ElasticSearchable<String> {

    @Value("${BONSAI_URL}")
    private String herokuBonsaiUrl;

    @Value("${BONSAI_USERNAME}")
    private String herokuBonsaiUserName;

    @Value("${BONSAI_PASSWORD}")
    private String herokuBonsaiPassword;

    private RemoteRestClient restClient;

    @Autowired
    public HerokuBonsaiElasticSearchDocumentService(RemoteRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ResponseEntity<String> addRestaurantDocument(
            Long id, FlattenedRestaurant flattenedRestaurant) {
        HttpEntity<FlattenedRestaurant> requestWithHeaders =
                restClient.getHttpRequestEntityForExchange(flattenedRestaurant, getAuthHeaders());
        return restClient
                .getHttpsRestTemplate()
                .exchange(
                        herokuBonsaiUrl.concat("/restaurants/restaurant/").concat(Long.toString(id)),
                        HttpMethod.POST,
                        requestWithHeaders,
                        String.class);
    }

    private Map<String, String> getAuthHeaders() {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            String base64EncodedValue =
                    new String(
                            encoder.encode(
                                    (herokuBonsaiUserName + ":" + herokuBonsaiPassword).getBytes("UTF-8")));
            return ImmutableMap.of("Authorization", "Basic " + base64EncodedValue);
        } catch (Exception e) {
            log.error("Error generating auth header", e);
        }
        return new HashMap<>();
    }
}
