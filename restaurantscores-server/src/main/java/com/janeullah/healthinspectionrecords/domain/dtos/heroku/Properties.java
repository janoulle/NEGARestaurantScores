package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Properties {

    @SerializedName("address")
    @Expose
    private Structure address;
    @SerializedName("county")
    @Expose
    private Structure county;
    @SerializedName("dateReported")
    @Expose
    private Structure dateReported;
    @SerializedName("id")
    @Expose
    private Structure id;
    @SerializedName("name")
    @Expose
    private Structure name;
    @SerializedName("nameKey")
    @Expose
    private Structure nameKey;
    @SerializedName("nonCriticalViolations")
    @Expose
    private Structure nonCriticalViolations;
    @SerializedName("criticalViolations")
    @Expose
    private Structure criticalViolations;
    @SerializedName("score")
    @Expose
    private Structure score;

}
