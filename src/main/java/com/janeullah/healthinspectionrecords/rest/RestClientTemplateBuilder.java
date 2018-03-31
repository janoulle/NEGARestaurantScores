package com.janeullah.healthinspectionrecords.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * https://stackoverflow.com/questions/42323468/how-to-call-https-restful-web-services-using-spring-resttemplate
 * Author: Jane Ullah
 * Date:  9/24/2017
 */
@Slf4j
@Component
public class RestClientTemplateBuilder {

    public RestTemplate httpsRestTemplate() {
        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);
            return new RestTemplate(requestFactory);
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException ex) {
            log.error("Failed to configure rest template", ex);
            throw new IllegalArgumentException(ex);
        }
    }

    public RestTemplate httpRestTemplate() {
        return new RestTemplate();
    }
}
