package com.janeullah.healthinspectionrecords.domain.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class County {
  private String name;
  private Map<String, FlattenedRestaurant> restaurants;
}
