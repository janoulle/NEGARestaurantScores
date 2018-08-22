package com.janeullah.healthinspectionrecords.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FlattenedInspectionReport  implements Serializable {
    private static final long serialVersionUID = 2824702648593960917L;
    private String name;
    private Long id;
    private int score;
    private String dateReported;
    private List<FlattenedViolation> violations;
}
