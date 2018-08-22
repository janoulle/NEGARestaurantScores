package com.janeullah.healthinspectionrecords.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Author: Jane Ullah Date: 9/18/2016
 */
@Data
@ToString(exclude = "restaurant")
@Entity
@Table(name = "ir_establishmentinfo")
public class EstablishmentInfo implements Serializable {

    private static final long serialVersionUID = 1228415070527155322L;
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
}
