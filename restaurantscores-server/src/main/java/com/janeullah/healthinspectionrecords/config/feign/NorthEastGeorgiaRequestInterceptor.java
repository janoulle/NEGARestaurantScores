package com.janeullah.healthinspectionrecords.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;

//https://github.com/OpenFeign/feign/blob/master/core/src/test/java/feign/FeignTest.java
public class NorthEastGeorgiaRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("User-Agent", System.getenv("USER_AGENT"));
    }
}