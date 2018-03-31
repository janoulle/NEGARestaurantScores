package com.janeullah.healthinspectionrecords.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/** Author: Jane Ullah Date: 9/18/2016 */
@Entity
@Table(name = "ir_establishmentinfo")
public class EstablishmentInfo implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "address", columnDefinition = "varchar(10000)")
  private String address;

  @Column(name = "county", columnDefinition = "varchar(500)")
  private String county;

  @Column(name = "name", columnDefinition = "varchar(1000)")
  private String name;

  @JsonIgnore
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "restaurant_id", foreignKey = @ForeignKey(name = "FK_restaurant_id"))
  private Restaurant restaurant;

  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return String.format("EstablishmentInfo[name=%s,address=%s,county=%s]", name, address, county);
  }

  public Restaurant getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(Restaurant restaurant) {
    this.restaurant = restaurant;
  }
}
