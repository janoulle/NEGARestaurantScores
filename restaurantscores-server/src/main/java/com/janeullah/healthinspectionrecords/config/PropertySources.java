package com.janeullah.healthinspectionrecords.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

// https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
// https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html
// https://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-injecting-property-values-into-configuration-beans/
@Configuration
@PropertySource(
        value = {
                "classpath:application-${spring.profiles.active}.properties",
                "file:${catalina.home:}/conf/catalina.properties",
        },
        ignoreResourceNotFound = true
)
public class PropertySources {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
