package com.janeullah.healthinspectionrecords.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource(
  value = {
    "classpath:application-${spring.profiles.active}.properties",
    "file:${catalina.home:}/conf/catalina.properties",
  },
  ignoreResourceNotFound = true
)
public class PropertySources {

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
