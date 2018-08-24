package com.janeullah.healthinspectionrecords.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "restaurantscores.threading")
public class ThreadProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int maxThreads;
}
