package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.util.ExecutorUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ThreadCleanup implements ServletContextListener {
  @Override
  public void contextDestroyed(ServletContextEvent evt) {
    ExecutorUtil.shutdown();
  }

  @Override
  public void contextInitialized(ServletContextEvent evt) {}
}
