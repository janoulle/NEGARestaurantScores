package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Restaurants {

    @SerializedName("aliases")
    @Expose
    private Aliases aliases;
    @SerializedName("mappings")
    @Expose
    private Mappings mappings;
    @SerializedName("settings")
    @Expose
    private Settings settings;

}
