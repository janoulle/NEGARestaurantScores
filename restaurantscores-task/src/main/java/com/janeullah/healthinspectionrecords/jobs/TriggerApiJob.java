package com.janeullah.healthinspectionrecords.jobs;

import com.janeullah.healthinspectionrecords.config.ConnectionInformation;
import com.janeullah.healthinspectionrecords.constants.HttpHeaders;
import com.janeullah.healthinspectionrecords.constants.QueueNames;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TriggerApiJob implements Job {

    private ConnectionInformation connectionInformation;

    public TriggerApiJob() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        ConnectionInformation information = new ConnectionInformation();
        information.setCloudAmpqUrl(System.getenv("CLOUDAMQP_URL"));
        information.setNegaUpdateUrl(System.getenv("NEGA_UPDATE_URL"));
        this.connectionInformation = information;
    }

    @Autowired
    public TriggerApiJob(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            ConnectionFactory factory = connectionInformation.getConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            Map<String, Object> params = new HashMap<>();
            params.put(HttpHeaders.HA_POLICY.getName(), "all");
            channel.queueDeclare(QueueNames.TRIGGER_API_QUEUE.getName(), true, false, false, params);

            String msg = "type=\"runAllUpdates\" currentTimeMillis=" + System.currentTimeMillis();
            byte[] body = msg.getBytes(StandardCharsets.UTF_8);
            channel.basicPublish("", QueueNames.TRIGGER_API_QUEUE.getName(), MessageProperties.PERSISTENT_TEXT_PLAIN, body);
            log.info("event=\"Message Sent\" {}", msg);
            connection.close();
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
