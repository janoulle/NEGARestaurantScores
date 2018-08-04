package com.janeullah.healthinspectionrecords.domain.dtos.heroku;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * https://docs.bonsai.io/docs/error-codes
 * https://docs.bonsai.io/docs/getting-started-with-heroku
 */
@Builder
@Data
public class HerokuBonsaiErrorResponse {
    @SerializedName("error")
    @Expose
    private ErrorResponse error;

    @SerializedName("status")
    @Expose
    private Integer status;
}
