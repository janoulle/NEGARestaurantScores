package com.janeullah.healthinspectionrecords.exceptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class HerokuClientException extends Exception {
    private int status;
    private String errorType;
    private JsonObject jsonResponse;

    public HerokuClientException(int status, String reason, Response.Body responseBody) {
        super(reason);
        this.status = status;
        this.errorType = extractResponseData(responseBody);
    }

    public int getStatus() {
        return status;
    }

    public String getErrorType() {
        return errorType;
    }

    /**
     * https://github.com/json-path/JsonPath
     *
     * @param responseBody Response.Body data wrapped by Feign
     */
    String extractResponseData(Response.Body responseBody) {
        try (InputStream stream = responseBody.asInputStream()) {
            byte[] bodyData = Util.toByteArray(stream);
            String data = new String(bodyData);

            this.jsonResponse = new JsonParser().parse(data).getAsJsonObject();
            log.error("message=\"Heroku client exception\" response={}", jsonResponse);

            return getErrorFromResponse();

        } catch (NullPointerException | IOException e) {
            log.error("Error reading response from Feign Exception", e);
        }
        return "";
    }

    private String getErrorFromResponse() {
        JsonElement errorObject = jsonResponse.get("error");
        if (errorObject == null) {
            // should be true for non-404 errors
            return jsonResponse.get("code").getAsString();
        } else {
            JsonObject obj = errorObject.getAsJsonObject();
            if (obj.get("type") == null) {
                // true for when the heroku app is 'sleeping'
                return obj.getAsString();
            } else {
                // true for the expected errors error.type
                return obj.get("type").getAsString();
            }
        }
    }
}
