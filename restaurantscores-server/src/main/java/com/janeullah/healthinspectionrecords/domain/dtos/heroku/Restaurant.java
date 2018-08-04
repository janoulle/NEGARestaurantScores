package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class Restaurant {

    @SerializedName("properties")
    @Expose
    private Properties properties;
}
