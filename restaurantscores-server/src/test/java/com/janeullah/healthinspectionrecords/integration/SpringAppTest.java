package com.janeullah.healthinspectionrecords.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * https://spring.io/guides/gs/testing-web/
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {"restaurantscores.threading.corePoolSize=5","restaurantscores.threading.maxPoolSize=10", "restaurantscores.threading.queueCapacity=500","restaurantscores.threading.maxThreads=5","USER_AGENT=REPLACE_ME", "DATA_EXPIRATION_IN_DAYS=7", "WATCHABLE_EVENT=REPLACE_ME", "NEGA_BUCKET_NAME_READONLY=REPLACE_ME", "NEGA_BUCKET_KEY=REPLACE_ME", "NEGA_FIREBASE_DB=REPLACE_ME", "BONSAI_URL=REPLACE_ME", "AWS_ES_URL=REPLACE_ME", "NEGA_BUCKET_ACCESS_KEY=REPLACE_ME", "NEGA_BUCKET_SECRET_KEY=REPLACE_ME", "AWS_ES_NEGA_ACCESS_KEY=REPLACE_ME", "AWS_ES_NEGA_SECRET=REPLACE_ME", "BONSAI_USERNAME=REPLACE_ME", "BONSAI_PASSWORD=REPLACE_ME", "DOWNLOAD_OVERRIDE=false", "AWS_ES_REGION_NAME=REPLACE_ME", "AWS_ES_SERVICE_NAME=REPLACE_ME", "JDBC_DATABASE_URL=REPLACE_ME", "JDBC_DATABASE_USERNAME=REPLACE_ME", "JDBC_DATABASE_PASSWORD=REPLACE_ME", "RELATIVE_PATH_TO_PAGE_STORAGE=path/ends/in/", "APP_DATA_FOLDER=path/to/folder/restaurantscores", "LOCAL_ES_URL=http://localhost:9200", "POSTGRES_DB=healthinspections", "POSTGRES_USER=postgres", "POSTGRES_PASSWORD=YOUR_PASSWORD"})
public class SpringAppTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void invocationOfIsAliveEndpointShouldReturnOk() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/healthcheck/isAlive",
                String.class)).contains("OK");
    }
}