package com.janeullah.healthinspectionrecords.config.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.URISyntaxException;

/**
 * Author: Jane Ullah Date: 9/20/2016
 * https://devcenter.heroku.com/articles/heroku-postgresql#connecting-in-java
 * https://medium.com/@joeclever/using-multiple-datasources-with-spring-boot-and-spring-data-6430b00c02e7
 * Opted to keep username/password out of .properties files and using env variables passed (e.g.
 * catalina.properties or intellij config
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.janeullah.healthinspectionrecords.repository"})
public class DataSourceConfig {

  @Autowired private Environment env;

  @Bean(name = "dataSource")
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() throws URISyntaxException {
    /*String profile = env.getProperty("spring.profiles.active");
    log.info("Active profile = {}", profile);
    if ("h2".equalsIgnoreCase(profile)) {
      return DataSourceBuilder.create()
          .driverClassName("org.h2.Driver")
          .url("jdbc:h2:mem:testdb")
          .username("sa")
          .password(StringUtils.EMPTY)
          .build();
    } else if ("sqlite".equalsIgnoreCase(profile)) {
      return DataSourceBuilder.create()
          .driverClassName("org.sqlite.JDBC")
          .url("jdbc:sqlite:" + env.getProperty("SQLITE_DB_PATH", "nega_inspections.db"))
          .build();
    } else {
      URI dbUri = new URI(env.getProperty("DATABASE_URL"));
      log.debug("Database url info = {}", dbUri);
      String[] userInfo = dbUri.getUserInfo().split(":");
      String prefix = "mysql".equalsIgnoreCase(profile) ? "mysql" : "postgresql";
      String dbUrl =
          String.format(
              "jdbc:%s://%s:%d%s", prefix, dbUri.getHost(), dbUri.getPort(), dbUri.getPath());

      return DataSourceBuilder.create()
              .url(dbUrl)
              .username(userInfo[0])
              .password(userInfo[1])
              .build();
    }*/
    return DataSourceBuilder.create().build();
  }
}
