package com.janeullah.healthinspectionrecords.services.impl;

import com.google.common.collect.ImmutableMap;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.rest.RemoteRestClient;
import com.janeullah.healthinspectionrecords.services.ElasticSearchDocumentService;
import com.janeullah.healthinspectionrecords.services.ElasticSearchable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class LocalhostElasticSearchDocumentService extends ElasticSearchDocumentService  implements ElasticSearchable<String> {

  @Value("${LOCAL_ES_URL}")
  private String localhostUrl;

  public LocalhostElasticSearchDocumentService() {}

  @Autowired
  public LocalhostElasticSearchDocumentService(RemoteRestClient restClient,
                                               RestaurantRepository restaurantRepository) {
    super(restClient, restaurantRepository);
  }

  @PostConstruct
  private String getLocalhostUrl() {
    return localhostUrl.concat("/restaurants/restaurant/{id}");
  }

  // replace pathvariable with map value
  @Override
  public ResponseEntity<String> addRestaurantDocument(
      Long id, FlattenedRestaurant flattenedRestaurant) {
    Map<String, Long> vars = ImmutableMap.of("id", id);
    return restClient
        .getRestTemplate()
        .postForEntity(getLocalhostUrl(), flattenedRestaurant, String.class, vars);
  }

  @Override
  public ResponseEntity<String> addRestaurantDocuments(List<FlattenedRestaurant> restaurants) {
    return null;
  }


}
