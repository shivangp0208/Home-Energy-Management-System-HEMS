package com.project.hems.SiteManagerService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "solar")
@Getter
@Setter
@ToString
public class Solar {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID solarId;

    @Column(name = "total_panel_capacity", nullable = false, updatable = true)
    private double totalPanelCapacityW;
    
    @Column(name = "inverter_max_capacity", nullable = false, updatable = true)
    private double inverterMaxCapacityW;
    
    @Column(name = "orientation", nullable = false, updatable = true)
    private String orientation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false, updatable = false)
    @JsonBackReference 
    private Site site;

}
