package com.janeullah.healthinspectionrecords.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FlattenedInspectionReport {
  private String name;
  private Long id;
  private int score;
  private String dateReported;
  private List<FlattenedViolation> violations;
}
