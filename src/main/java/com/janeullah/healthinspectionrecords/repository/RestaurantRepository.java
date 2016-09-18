package com.janeullah.healthinspectionrecords.repository;


import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html_single/#queryhql-joins
 * https://docs.jboss.org/hibernate/orm/4.0/manual/en-US/html/queryhql.html#queryhql-joins-forms
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-property-expressions
 */
@Repository
public interface  RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByEstablishmentInfoNameIgnoreCase (String name);
    List<Restaurant> findByEstablishmentInfoCountyIgnoreCase (String county);
    List<Restaurant> findByEstablishmentInfoNameAndEstablishmentInfoCountyIgnoreCase (String name, String county);
    List<Restaurant> findByEstablishmentInfoNameContaining(String name);

    @Query("select r from InspectionReport ir inner join ir.restaurant r where ir.score >= :limit")
    List<Restaurant> findRestaurantsWithScoresGreaterThanOrEqual(@Param("limit") int limit);

    @Query("select r from InspectionReport ir inner join ir.restaurant r where ir.score <= :limit")
    List<Restaurant> findRestaurantsWithScoresLessThanOrEqual(@Param("limit") int limit);

    @Query("select r from InspectionReport ir inner join ir.restaurant r where ir.score between :lower and :upper")
    List<Restaurant> findRestaurantsWithScoresBetween(@Param("lower") int lower, @Param("upper") int upper);

    @Query("select r from Violation v inner join v.inspectionReport ir inner join ir.restaurant r where v.severity = 3")
    List<Restaurant> findRestaurantsWithCriticalViolations();
}
