package com.janeullah.healthinspectionrecords.rest;

import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * https://stackoverflow.com/questions/10358345/making-authenticated-post-requests-with-spring-resttemplate-for-android
 */
@Component
public class RemoteRestClient {
  private RestTemplate restTemplate;
  private RestTemplate restTemplateHttps;

  @Autowired
  public RemoteRestClient(RestClientTemplateBuilder restClientTemplateBuilder) {
    this.restTemplate = restClientTemplateBuilder.httpRestTemplate();
    this.restTemplateHttps = restClientTemplateBuilder.httpsRestTemplate();
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public RestTemplate getHttpsRestTemplate() {
    return restTemplateHttps;
  }

  public <T> HttpEntity<T> getHttpRequestEntityForExchange(
      T request, Map<String, String> requestHeaders) {
    Assert.notNull(request, "request may not be null");
    Assert.notNull(requestHeaders, "Headers may not be null");
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", ContentType.APPLICATION_JSON.toString());
    requestHeaders.forEach(headers::add);
    return new HttpEntity<>(request, headers);
  }
}
