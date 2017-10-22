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

}
