package com.janeullah.healthinspectionrecords.exceptions;

import feign.Response;
import feign.Util;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Getter
@Slf4j
public class NorthEastGeorgiaWebPageDownloadException extends Exception {
    private static final long serialVersionUID = 1015190425473396324L;
    private final int status;
    private final String body;

    public NorthEastGeorgiaWebPageDownloadException(int status, String reason, Response.Body responseBody) {
        super(reason);
        this.status = status;
        this.body = getResponseAsString(responseBody);
    }

    private String getResponseAsString(Response.Body responseBody) {
        try (InputStream stream = responseBody.asInputStream()) {
            byte[] bodyData = Util.toByteArray(stream);
            return new String(bodyData);
        } catch (Exception e) {
            log.error("Exception reading error response from stream", e);
        }
        return "";
    }
}