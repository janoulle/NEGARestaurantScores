package com.janeullah.healthinspectionrecords.workers;

import com.janeullah.healthinspectionrecords.config.ConnectionInformation;
import com.janeullah.healthinspectionrecords.constants.HttpHeaders;
import com.janeullah.healthinspectionrecords.constants.QueueNames;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TriggerApiWorker {

    private ConnectionInformation connectionInformation;

    public TriggerApiWorker(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
    }

    void listenForConnections() {
        Map<String, Object> params = new HashMap<>();
        params.put(HttpHeaders.HA_POLICY.getName(), "all");
        try {
            ConnectionFactory factory = connectionInformation.getConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QueueNames.TRIGGER_API_QUEUE.getName(), true, false, false, params);

            final Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);

                    log.info("event=\"Message Received\" message={} at currentTimeMillis={}", message, System.currentTimeMillis());
                    try {
                        doWork(message);
                    } finally {
                        log.info("event=\"Work Completed\" currentTimeMillis={}", System.currentTimeMillis());
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            };

            channel.basicConsume(QueueNames.TRIGGER_API_QUEUE.getName(), false, consumer);

        } catch (IOException e) {
            log.error("Unable to consume or open channel with connection for cloudAmpqUrl={} queueName={} params={}", connectionInformation.getCloudAmpqUrl(), QueueNames.TRIGGER_API_QUEUE.getName(), params, e);
        }
    }

    private void doWork(String message) {
        try{
            URL urlObj = new URL(connectionInformation.getNegaUpdateUrl());
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.connect();
            int code = conn.getResponseCode();
            log.info("sentMessage={} statusCode={} responseMessage={}", message, code, conn.getResponseMessage());

        } catch (IOException e) {
            log.error("Error connecting to remote update server", e);
        }

    }

}

