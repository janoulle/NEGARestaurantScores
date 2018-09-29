package com.janeullah.healthinspectionrecords.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Component
public class ConnectionInformation {

    @Value("${CLOUDAMQP_URL}")
    private String cloudAmpqUrl;

    @Value("${NEGA_UPDATE_URL}")
    private String negaUpdateUrl;

    private ConnectionFactory connectionFactory = new ConnectionFactory();

    @PostConstruct
    private void setUri() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        connectionFactory.setUri(cloudAmpqUrl);
    }

    public void setCloudAmpqUrl(String url) throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        this.cloudAmpqUrl = url;
        connectionFactory.setUri(url);
    }

    public void setNegaUpdateUrl(String url) {
        this.negaUpdateUrl = url;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public String getCloudAmpqUrl() {
        return cloudAmpqUrl;
    }

    public String getNegaUpdateUrl() { return negaUpdateUrl; }
}
