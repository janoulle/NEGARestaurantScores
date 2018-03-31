package com.janeullah.healthinspectionrecords;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@Slf4j
@ComponentScan(basePackages = {"com.janeullah.healthinspectionrecords"})
@SpringBootApplication
@EnableCaching
public class RestaurantScoresApplication {

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RestaurantScoresApplication.class, args);
        log.debug("Beans provided by the Restaurant Scores application");

        Arrays.stream(ctx.getBeanDefinitionNames())
                .sorted()
                .forEach(log::debug);
    }
}
