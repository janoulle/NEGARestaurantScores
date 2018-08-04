package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RootCause {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("reason")
    @Expose
    private String reason;

    @SerializedName("resource.type")
    @Expose
    private String resourceType;

    @SerializedName("resource.id")
    @Expose
    private String resourceId;

    @SerializedName("index")
    @Expose
    private String index;

    @SerializedName("index_uuid")
    @Expose
    private String indexUuid;
}
