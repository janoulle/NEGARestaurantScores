package com.janeullah.healthinspectionrecords.services;

import com.janeullah.healthinspectionrecords.rest.RemoteRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html
 * Author: jane
 * Date:  9/23/2017
 */
@Service
public abstract class ElasticSearchDocumentService{

    protected RemoteRestClient restClient;

    public ElasticSearchDocumentService(){}

    @Autowired
    public ElasticSearchDocumentService(RemoteRestClient restClient){
        this.restClient = restClient;
    }


    /*private static final String HEROKU_BONSAI_URL = System.getenv("BONSAI_URL").concat("/restaurants/restaurant/{id}");
    private static final String LOCALHOST_ELASTICSEARCH_TYPE_URL = "http://localhost:9200/restaurants/restaurant/{id}";
    private static final String LOCALHOST_ELASTICSEARCH_INDEX_URL = "http://localhost:9200/restaurants";

    //replace pathvariable with map value
    public ResponseEntity<String> addDocument(Long id, FlattenedRestaurant flattenedRestaurant) {
        Map<String, Long> vars = ImmutableMap.of("id",id);
        return restTemplate.postForEntity(getUrlByProfile(), flattenedRestaurant, String.class, vars);
    }

    private String getUrlByProfile(){
        if ("Y".equalsIgnoreCase(System.getenv("RUN_HEROKU_INSERTS")) ||
                Arrays.stream(env.getActiveProfiles()).anyMatch("heroku"::equalsIgnoreCase)){
            return HEROKU_BONSAI_URL;
        }
        return LOCALHOST_ELASTICSEARCH_TYPE_URL;
    }

    public ResponseEntity<HttpStatus> deleteRestaurantIndex(){
        try {
            restTemplate.delete(new URI(LOCALHOST_ELASTICSEARCH_INDEX_URL));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            throw new IllegalArgumentException(e);
        }
    }*/
}
