package com.janeullah.healthinspectionrecords.services.internal;

import com.janeullah.healthinspectionrecords.domain.entities.Violation;

import java.util.List;

public interface ViolationService {
    Violation findById(long id);

    List<Violation> findByCategory(String category);

    List<Violation> findViolationsByRestaurantId(Long id);

    List<Violation> findAll();
}
