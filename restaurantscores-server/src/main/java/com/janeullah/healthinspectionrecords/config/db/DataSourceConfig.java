package com.janeullah.healthinspectionrecords.config.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Author: Jane Ullah Date: 9/20/2016 2.x
 * https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-two-datasources
 * https://github.com/spring-projects/spring-boot/issues/12758
 *
 * <p>1.5.x https://devcenter.heroku.com/articles/heroku-postgresql#connecting-in-java
 * https://medium.com/@joeclever/using-multiple-datasources-with-spring-boot-and-spring-data-6430b00c02e7
 * Opted to keep username/password out of .properties files and using env variables passed (e.g.
 * catalina.properties or intellij config
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.janeullah.healthinspectionrecords.repository"})
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }
}
