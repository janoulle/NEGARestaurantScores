package com.janeullah.healthinspectionrecords.domain.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FlattenedViolation {
  private Long violationId;
  private String severity;
  private String category;
  private String section;
  private String inspectionType;
  private String summary;
  private String notes;
}
