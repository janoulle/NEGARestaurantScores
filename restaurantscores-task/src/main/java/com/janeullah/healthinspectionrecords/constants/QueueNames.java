package com.janeullah.healthinspectionrecords.constants;

public enum QueueNames {
    TRIGGER_API_QUEUE("trigger-api-queue-1");

    private String queueName;

    QueueNames(String queueName) {
        this.queueName = queueName;
    }

    public String getName() {
        return queueName;
    }
}
