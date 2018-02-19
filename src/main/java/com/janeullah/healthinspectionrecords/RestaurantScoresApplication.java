package com.janeullah.healthinspectionrecords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@ComponentScan(basePackages = {"com.janeullah.healthinspectionrecords"})
@SpringBootApplication
@EnableCaching
public class RestaurantScoresApplication {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantScoresApplication.class);

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RestaurantScoresApplication.class, args);
        logger.debug("Beans provided by the Restaurant Scores application");

        Arrays.stream(ctx.getBeanDefinitionNames())
                .sorted()
                .forEach(logger::debug);
    }
}
