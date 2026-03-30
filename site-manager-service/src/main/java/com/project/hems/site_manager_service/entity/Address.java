package com.project.hems.site_manager_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "address")
@Getter
@Setter
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private UUID addressId;

    @Column(name = "address_line_1", nullable = false, updatable = true)
    private String addressLine1;

    @Column(name = "address_line_2", updatable = true)
    private String addressLine2;

    @Column(name = "city", updatable = true, nullable = false)
    private String city;

    @Column(name = "state", updatable = true, nullable = false)
    private String state;

    @Column(name = "postal_code", updatable = true, nullable = false)
    private String postalCode;

    @Column(name = "country", updatable = true, nullable = false)
    private String country;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false, updatable = false)
    @JsonBackReference
    private Site site;

}
