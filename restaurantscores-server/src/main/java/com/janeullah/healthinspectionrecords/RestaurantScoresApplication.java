package com.janeullah.healthinspectionrecords;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@Slf4j
@EnableFeignClients
@EnableCaching
@SpringBootApplication
public class RestaurantScoresApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RestaurantScoresApplication.class, args);
        log.debug("Beans provided by the Restaurant Scores application");

        Arrays.stream(ctx.getBeanDefinitionNames()).sorted().forEach(log::debug);
    }
}
