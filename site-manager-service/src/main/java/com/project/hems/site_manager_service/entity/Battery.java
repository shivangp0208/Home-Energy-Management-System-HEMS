package com.project.hems.site_manager_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "battery")
@Getter
@Setter
@ToString
public class Battery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID batteryId;
    
    @Column(name = "capacity_wh", nullable = false)
    private double capacityWh;
    
    @Column(name = "max_output_w", nullable = false)
    private double maxOutputW;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "site_id", nullable = false, updatable = false)
    @ToString.Exclude
    private Site site;
}
