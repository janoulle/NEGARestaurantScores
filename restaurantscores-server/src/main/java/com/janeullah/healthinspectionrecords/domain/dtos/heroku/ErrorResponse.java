package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ErrorResponse {
    @SerializedName("root_cause")
    @Expose
    private List<RootCause> rootCause;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("caused_by")
    @Expose
    private RootCause causedBy;
    @SerializedName("resource.type")
    @Expose
    private String resourceType;

    @SerializedName("resource.id")
    @Expose
    private String resourceId;

    @SerializedName("index")
    @Expose
    private String index;
}
