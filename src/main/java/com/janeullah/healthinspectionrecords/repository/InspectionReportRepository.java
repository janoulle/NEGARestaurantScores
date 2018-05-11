package com.janeullah.healthinspectionrecords.repository;

import com.janeullah.healthinspectionrecords.domain.entities.InspectionReport;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * http://docs.spring.io/spring-data/jpa/docs/1.4.3.RELEASE/reference/html/jpa.repositories.html
 * Author: Jane Ullah Date: 9/19/2016
 */
@Repository
public interface InspectionReportRepository extends JpaRepository<InspectionReport, Long> {
  List<InspectionReport> findByRestaurant(Restaurant restaurant);

  // https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/
  List<InspectionReport> findByScoreGreaterThan(int marker);

  List<InspectionReport> findByScoreBetween(int start, int stop);
}
