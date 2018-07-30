package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HerokuSleepingClusterErrorResponse {
    @SerializedName("error")
    @Expose
    private String error;

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("exception")
    @Expose
    private Exception exception;
}
