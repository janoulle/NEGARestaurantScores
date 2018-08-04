package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Structure {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("fields")
    @Expose
    private Fields fields;
}

