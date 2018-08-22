package com.janeullah.healthinspectionrecords.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * https://dzone.com/articles/spring-hibernate-ehcache-caching
 * http://search.maven.org/#search%7Cga%7C1%7Ca%3Aehcache-core
 * https://blog.trifork.com/2015/02/09/active-cache-eviction-with-ehcache-and-spring-framework/
 * https://www.foreach.be/blog/spring-cache-annotations-some-tips-tricks
 * https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/integration.html#cache-annotations-cacheable-synchronized
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class CacheConfiguration {

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactory() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehCacheManagerFactory().getObject());
        cacheManager.setTransactionAware(true);
        return cacheManager;
    }
}
