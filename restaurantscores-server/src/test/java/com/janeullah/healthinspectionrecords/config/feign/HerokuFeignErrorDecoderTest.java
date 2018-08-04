package com.janeullah.healthinspectionrecords.config.feign;

import com.janeullah.healthinspectionrecords.exceptions.HerokuClientException;
import feign.FeignException;
import feign.Response;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class HerokuFeignErrorDecoderTest {

    @Test
    public void testDecodeResponse_4xx() {
        HerokuFeignErrorDecoder errorDecoder = new HerokuFeignErrorDecoder();
        Response response = Response.builder()
                .status(400)
                .headers(new HashMap<>())
                .build();
        Exception actualException = errorDecoder.decode("dummyMethod", response);
        assertTrue(actualException instanceof HerokuClientException);
    }

    @Test
    public void testDecodeResponse_Not4xx() {
        HerokuFeignErrorDecoder errorDecoder = new HerokuFeignErrorDecoder();
        Response response = Response.builder()
                .status(501)
                .headers(new HashMap<>())
                .build();
        Exception actualException = errorDecoder.decode("dummyMethod", response);
        assertTrue(actualException instanceof FeignException);
    }
}
