package com.janeullah.healthinspectionrecords.config.feign;

import com.janeullah.healthinspectionrecords.exceptions.HerokuClientException;
import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * https://github.com/OpenFeign/feign/issues/402
 * https://github.com/OpenFeign/feign/wiki/Custom-error-handling
 * https://source.coveo.com/2016/02/19/microservices-and-exception-handling/
 */
public class HerokuFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 499) {
            return new HerokuClientException(
                    response.status(),
                    response.reason(),
                    response.body()
            );
        }
        return new ErrorDecoder.Default().decode(methodKey, response);
    }

}