package com.janeullah.healthinspectionrecords.services;

import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.rest.RemoteRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html Author: Jane Ullah Date:
 * 9/23/2017
 */
@Service
public abstract class ElasticSearchDocumentService{

  protected RemoteRestClient restClient;
  protected RestaurantRepository restaurantRepository;

  public ElasticSearchDocumentService() {}

  @Autowired
  public ElasticSearchDocumentService(RemoteRestClient restClient,
                                      RestaurantRepository restaurantRepository) {
    this.restClient = restClient;
    this.restaurantRepository = restaurantRepository;
  }
}
