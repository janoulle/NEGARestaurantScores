package com.janeullah.healthinspectionrecords.domain.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FlattenedInspectionReport {
  private String name;
  private Long id;
  private int score;
  private String dateReported;
  private List<FlattenedViolation> violations;
}
