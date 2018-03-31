package com.janeullah.healthinspectionrecords.util;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.HttpMethodName;
import org.apache.commons.io.IOUtils;

import java.net.URI;

/**
 * https://gist.githubusercontent.com/devilelephant/4ae94f8e9f24b6056733/raw/9b9d48ab8c0ef6e463034e1e4838c210d97ac819/AWS4Signer.groovy
 */
public class AwsV4RequestSigner {
  private final String regionName;
  private final String serviceName;
  private final AWSCredentials awsCredentials;

  public AwsV4RequestSigner(AWSCredentials awsCredentials, String regionName, String serviceName) {
    this.regionName = regionName;
    this.serviceName = serviceName;
    this.awsCredentials = awsCredentials;
  }

  public Request<Void> signRequest(Request<Void> signableRequest) {
    AWS4Signer signer = new AWS4Signer(false);
    signer.setRegionName(regionName);
    signer.setServiceName(serviceName);
    signer.sign(signableRequest, awsCredentials);
    return signableRequest;
  }

  public <T> Request<Void> makeSignableRequest(T awsSearchRequest, String url) {
    Request<Void> request = new DefaultRequest<>("es"); // Request to ElasticSearch
    request.setHttpMethod(HttpMethodName.POST);
    request.setEndpoint(URI.create(url));
    request.setContent(IOUtils.toInputStream(DataUtil.writeValueAsString(awsSearchRequest)));
    return request;
  }
}
