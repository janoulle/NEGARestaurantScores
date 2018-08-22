package com.janeullah.healthinspectionrecords.domain.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FlattenedViolation  implements Serializable {
    private static final long serialVersionUID = -8757266972065591423L;
    private Long violationId;
    private String severity;
    private String category;
    private String section;
    private String inspectionType;
    private String summary;
    private String notes;
}
