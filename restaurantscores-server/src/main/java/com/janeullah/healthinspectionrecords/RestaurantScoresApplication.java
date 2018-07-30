package com.janeullah.healthinspectionrecords;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.Arrays;
import java.util.concurrent.Executor;

@Slf4j
@EnableFeignClients
@EnableScheduling
@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.janeullah.healthinspectionrecords"})
public class RestaurantScoresApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RestaurantScoresApplication.class, args);
        log.debug("Beans provided by the Restaurant Scores application");

        Arrays.stream(ctx.getBeanDefinitionNames()).sorted().forEach(log::debug);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
