package com.janeullah.healthinspectionrecords.workers;

import com.janeullah.healthinspectionrecords.config.ConnectionInformation;

public class WorkerMain {

    public static void main(String[] args) throws Exception {
        ConnectionInformation connectionInformation = new ConnectionInformation();
        connectionInformation.setCloudAmpqUrl(System.getenv("CLOUDAMQP_URL"));
        connectionInformation.setNegaUpdateUrl(System.getenv("NEGA_UPDATE_URL"));

        TriggerApiWorker triggerApiWorker = new TriggerApiWorker(connectionInformation);
        triggerApiWorker.listenForConnections();
    }
}
