package com.janeullah.healthinspectionrecords;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@ComponentScan(basePackages = {"com.janeullah.healthinspectionrecords"})
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
