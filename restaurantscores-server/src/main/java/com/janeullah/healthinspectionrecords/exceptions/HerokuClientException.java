package com.janeullah.healthinspectionrecords.exceptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import feign.Response;
import feign.Util;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Getter
@Slf4j
public class HerokuClientException extends Exception {
    private static final long serialVersionUID = -8836461401798995942L;
    private final int status;
    private final String errorType;
    private final String jsonResponse;

    public HerokuClientException(int status, String reason, Response.Body responseBody) {
        super(reason);
        this.status = status;
        Optional<JsonObject> jsonErrorData = getResponseDataAsJsonObject(responseBody);
        this.errorType = jsonErrorData.map(this::getErrorFromResponse).orElse("");
        this.jsonResponse = jsonErrorData.isPresent() ? jsonErrorData.toString() : "";
    }

    private Optional<JsonObject> getResponseDataAsJsonObject(Response.Body responseBody) {
        try (InputStream stream = responseBody.asInputStream()) {
            byte[] bodyData = Util.toByteArray(stream);
            String data = new String(bodyData);

            JsonElement obj = new JsonParser().parse(data);
            log.error("message=\"Heroku client exception\" response={}", jsonResponse);
            return obj.isJsonObject() ? Optional.of(obj.getAsJsonObject()) : Optional.empty();
        } catch (JsonParseException | NullPointerException | IOException e) {

            log.error("Error reading response from Feign Exception", e);
        }
        return Optional.empty();
    }

    public String getErrorFromResponse(JsonObject jsonResponse) {
        JsonElement errorObject = jsonResponse.get("error");
        if (errorObject == null) {
            // should be true for non-404 errors
            return jsonResponse.get("code").getAsString();
        } else {
            JsonObject obj = errorObject.getAsJsonObject();
            if (obj.get("type") == null) {
                // true for when the heroku app is 'sleeping'
                return obj.toString();
            } else {
                // true for the expected errors error.type
                return obj.get("type").getAsString();
            }
        }
    }
}
