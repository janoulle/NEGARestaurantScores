package com.janeullah.healthinspectionrecords;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@Slf4j
@EnableScheduling
@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.janeullah.healthinspectionrecords"})
public class RestaurantScoresApplication {

  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(RestaurantScoresApplication.class, args);
    log.debug("Beans provided by the Restaurant Scores application");

    Arrays.stream(ctx.getBeanDefinitionNames()).sorted().forEach(log::debug);
  }
}
