package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Index {

    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    @SerializedName("number_of_shards")
    @Expose
    private String numberOfShards;
    @SerializedName("number_of_replicas")
    @Expose
    private String numberOfReplicas;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("version")
    @Expose
    private Version version;
    @SerializedName("provided_name")
    @Expose
    private String providedName;

}
