package com.janeullah.healthinspectionrecords.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class County {
    private String name;
    private Map<String, FlattenedRestaurant> restaurants;
}
