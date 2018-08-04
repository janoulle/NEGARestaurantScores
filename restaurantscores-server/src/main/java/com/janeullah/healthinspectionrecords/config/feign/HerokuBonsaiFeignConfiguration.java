package com.janeullah.healthinspectionrecords.config.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class HerokuBonsaiFeignConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    // https://github.com/OpenFeign/feign/issues/386
    @Bean
    public HerokuFeignErrorDecoder herokuFeignErrorDecoder() {
        return new HerokuFeignErrorDecoder();
    }

}
