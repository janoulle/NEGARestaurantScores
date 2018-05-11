package com.janeullah.healthinspectionrecords.repository;

import com.janeullah.healthinspectionrecords.domain.entities.InspectionReport;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.util.TestUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4
@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private InspectionReportRepository inspectionReportRepository;
  @Autowired private RestaurantRepository restaurantRepository;
  @Autowired private ViolationRepository violationRepository;

  @Before
  public void setup() {
    this.entityManager.persist(TestUtil.getSingleRestaurant());
  }

  @Test
  public void testFindByCategory_Success() {

    List<Violation> violations = this.violationRepository.findByCategory("4-D");

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.get(0).getCategory()).isEqualTo("4-D");
  }

  @Test
  public void testFindByCategory_NotFound() {

    List<Violation> violations = this.violationRepository.findByCategory("2-A");

    assertThat(violations.size()).isEqualTo(0);
  }

  @Ignore("Ignoring existing searches for now")
  @Test
  public void testFindViolationsByRestaurantId_Success() {

    List<Violation> violations = this.violationRepository.findViolationsByRestaurantId(1L);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.get(0).getCategory()).isEqualTo("4-D");
  }


  @Test
  public void testFindViolationsByRestaurantId_Fail() {

    List<Violation> violations = this.violationRepository.findViolationsByRestaurantId(2L);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  public void testFindByScoreGreaterThan_Found() {
    List<InspectionReport> inspectionReport =
        this.inspectionReportRepository.findByScoreGreaterThan(80);

    assertThat(inspectionReport.size()).isEqualTo(1);
    assertThat(inspectionReport.get(0).getScore()).isEqualTo(85);
  }

  @Test
  public void testFindByScoreGreaterThan_NotFound() {
    List<InspectionReport> inspectionReport =
            this.inspectionReportRepository.findByScoreGreaterThan(90);

    assertThat(inspectionReport.size()).isEqualTo(0);
  }

  @Test
  public void testFindByScoreBetween_Found() {
    List<InspectionReport> inspectionReport =
            this.inspectionReportRepository.findByScoreBetween(84,86);

    assertThat(inspectionReport.size()).isEqualTo(1);
    assertThat(inspectionReport.get(0).getScore()).isEqualTo(85);
  }

  @Ignore("Ignoring existing searches for now")
  @Test
  public void testFindByRestaurant_Success() {
    Restaurant r = TestUtil.getSingleRestaurant();
    r.setId(1L);
    List<InspectionReport> inspectionReport =
            this.inspectionReportRepository.findByRestaurant(r);

    assertThat(inspectionReport.size()).isEqualTo(1);
    assertThat(inspectionReport.get(0).getScore()).isEqualTo(85);

  }

  @Ignore("Ignoring existing searches for now")
  @Test
  public void testFindByRestaurant_NotFound() {
    Restaurant r = TestUtil.getSingleRestaurant();
    r.setId(2L);
    List<InspectionReport> inspectionReport =
            this.inspectionReportRepository.findByRestaurant(r);

    assertThat(inspectionReport.size()).isEqualTo(1);
    assertThat(inspectionReport.get(0).getScore()).isEqualTo(85);

  }

  @Test
  public void testFindByEstablishmentInfoNameIgnoreCase_Success() {
    List<Restaurant> restaurants = this.restaurantRepository.findByEstablishmentInfoNameIgnoreCase("ZAXBY'S-monroe");

    assertThat(restaurants.size()).isEqualTo(1);
    assertThat(restaurants.get(0).getEstablishmentInfo().getName()).isEqualTo("ZAXBY'S-MONROE");
  }
}
