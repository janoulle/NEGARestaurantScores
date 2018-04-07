package com.janeullah.healthinspectionrecords;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@ComponentScan(basePackages = {"com.janeullah.healthinspectionrecords"})
@PropertySource(value = {
        "classpath:application-${spring.profiles.active}.properties",
        "file:${catalina.home:}/conf/catalina.properties",
}, ignoreResourceNotFound = true)
@SpringBootApplication
@EnableCaching
public class RestaurantScoresApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(RestaurantScoresApplication.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(RestaurantScoresApplication.class, args);
  }
}
