package com.janeullah.healthinspectionrecords.domain.notifications;


/**
 * Author: jane
 * Date:  4/28/2017
 */
public class ErrorResponse {
    private String code;
    private String message;
    private Throwable throwable;

    public ErrorResponse(String code, String message, Throwable throwable){
        this.code = code;
        this.message = message;
        this.throwable = throwable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}