package com.janeullah.healthinspectionrecords.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * https://stackoverflow.com/questions/17285893/spring-equivalent-of-completionservice
 * https://stackoverflow.com/questions/24903658/spring-threadpooltaskexecutor-vs-java-executorservice-cachedthreadpool
 * https://spring.io/guides/gs/async-method/
 *
 */
@Configuration
@EnableAsync
public class ThreadConfiguration {

    private ThreadProperties threadProperties;

    @Autowired
    public ThreadConfiguration(ThreadProperties threadProperties) {
        this.threadProperties = threadProperties;
    }

    /*@Bean
    public ListeningExecutorService executorService() {
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadProperties.getMaxThreads()));
    }*/

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadProperties.getQueueCapacity());
        executor.setThreadNamePrefix("RestaurantScores-");
        executor.initialize();
        return executor;
    }
}
