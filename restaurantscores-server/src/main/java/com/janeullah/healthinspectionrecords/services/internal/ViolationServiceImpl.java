package com.janeullah.healthinspectionrecords.services.internal;

import com.janeullah.healthinspectionrecords.constants.CacheConstants;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ViolationServiceImpl implements ViolationService {
    private ViolationRepository violationRepository;

    @Autowired
    public ViolationServiceImpl(ViolationRepository violationRepository) {
        this.violationRepository = violationRepository;
    }

    @Override
    @Cacheable(value = CacheConstants.VIOLATION_BY_ID, key = "#id", unless = "#result != null")
    public Violation findById(long id) {
        Optional<Violation> violation = violationRepository.findById(id);
        return violation.orElse(null);
    }

    @Override
    @Cacheable(value = CacheConstants.VIOLATIONS_BY_CATEGORY, key = "#category")
    public List<Violation> findByCategory(String category) {
        return violationRepository.findByCategory(category);
    }

    @Override
    @Cacheable(value = CacheConstants.VIOLATIONS_BY_RESTAURANT_ID, key = "#id")
    public List<Violation> findViolationsByRestaurantId(Long id) {
        return violationRepository.findViolationsByRestaurantId(id);
    }

    @Override
    @Cacheable(value = CacheConstants.ALL_VIOLATIONS)
    public List<Violation> findAll() {
        return violationRepository.findAll();
    }
}
