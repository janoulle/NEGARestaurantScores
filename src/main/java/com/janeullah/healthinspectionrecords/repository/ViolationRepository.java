package com.janeullah.healthinspectionrecords.repository;

import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: Jane Ullah
 * Date:  9/19/2016
 */
@Repository
public interface ViolationRepository extends JpaRepository<Violation,Long> {
    List<Violation> findByCategory(String category);

    @Query("select v from Violation v inner join v.inspectionReport ir inner join ir.restaurant r where r.id = :id")
    List<Violation> findViolationsByRestaurantId(@Param("id") Long restaurantId);
}
