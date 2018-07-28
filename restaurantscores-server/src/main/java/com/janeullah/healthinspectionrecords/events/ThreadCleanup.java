package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.util.ExecutorUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
@WebListener
public class ThreadCleanup implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        ExecutorUtil.shutdown();
    }

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        log.info("context getting initialized");
    }
}
