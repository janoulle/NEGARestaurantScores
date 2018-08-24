package com.janeullah.healthinspectionrecords.config.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;

//https://github.com/OpenFeign/feign/issues/402
public class NorthEastGeorgiaFeignConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public NorthEastGeorgiaFeignErrorDecoder errorDecoder() {
        return new NorthEastGeorgiaFeignErrorDecoder();
    }

    @Bean
    public NorthEastGeorgiaRequestInterceptor requestInterceptor() {
        return new NorthEastGeorgiaRequestInterceptor();
    }
}
