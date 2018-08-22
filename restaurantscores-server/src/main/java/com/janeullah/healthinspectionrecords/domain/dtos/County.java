package com.janeullah.healthinspectionrecords.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class County  implements Serializable {
    private static final long serialVersionUID = 233517228589616346L;
    private String name;
    private Map<String, FlattenedRestaurant> restaurants;
}
