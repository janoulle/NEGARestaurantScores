package com.janeullah.healthinspectionrecords.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Author: Jane Ullah
 * Date:  9/20/2016
 * https://devcenter.heroku.com/articles/heroku-postgresql#connecting-in-java
 */
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        String profile = System.getenv("spring.profiles.active");
        if ("h2".equalsIgnoreCase(profile)) {
            return dataSourceBuilder
                    .driverClassName("org.h2.Driver")
                    .url("jdbc:h2:mem:testdb")
                    .username("sa")
                    .password(StringUtils.EMPTY)
                    .build();
        }else if ("sqlite".equalsIgnoreCase(profile)){
            return dataSourceBuilder
                    .driverClassName("org.sqlite.JDBC")
                    .url("jdbc:sqlite:"+System.getProperty("SQLITE_DB_PATH","nega_inspections.db"))
                    .build();
        }else {
            String[] userInfo = dbUri.getUserInfo().split(":");
            String prefix = "mysql".equalsIgnoreCase(profile) ? "mysql" : "postgresql";
            String dbUrl = String.format("jdbc:%s://%s:%d%s", prefix,dbUri.getHost(),dbUri.getPort(),dbUri.getPath());

            return dataSourceBuilder
                    .url(dbUrl)
                    .username(userInfo[0])
                    .password(userInfo[1])
                    .build();
        }
    }
}
