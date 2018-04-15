package com.janeullah.healthinspectionrecords;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
@Slf4j
@EnableCaching
@ComponentScan(basePackages = {"com.janeullah.healthinspectionrecords"})
@PropertySource(value = {
        "classpath:application-${spring.profiles.active}.properties",
        "file:${catalina.home:}/conf/catalina.properties",
}, ignoreResourceNotFound = true)
@SpringBootApplication
public class RestaurantScoresApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(RestaurantScoresApplication.class);
  }

  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(RestaurantScoresApplication.class, args);
    log.debug("Beans provided by the Restaurant Scores application");

    Arrays.stream(ctx.getBeanDefinitionNames())
            .sorted()
            .forEach(log::debug);
  }

}
