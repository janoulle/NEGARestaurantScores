package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Acknowledgement {
    @SerializedName("acknowledged")
    @Expose
    private Boolean acknowledged;
    @SerializedName("shards_acknowledged")
    @Expose
    private Boolean shardsAcknowledged;
}
