package com.janeullah.healthinspectionrecords.config.feign;

import com.janeullah.healthinspectionrecords.exceptions.NorthEastGeorgiaWebPageDownloadException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class NorthEastGeorgiaFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 599) {
            return new NorthEastGeorgiaWebPageDownloadException(
                    response.status(),
                    response.reason(),
                    response.body()
            );
        }
        return new ErrorDecoder.Default().decode(methodKey, response);
    }
}
