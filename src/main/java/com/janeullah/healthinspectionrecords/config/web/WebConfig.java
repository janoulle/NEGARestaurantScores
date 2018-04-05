package com.janeullah.healthinspectionrecords.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//https://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/mvc.html#mvc-default-servlet-handler
//https://stackoverflow.com/questions/28089129/spring-boot-deployed-in-tomcat-gives-404-but-works-stand-alone?rq=1
//https://stackoverflow.com/questions/21760201/404-error-for-tomcat-6-for-spring-application
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}
