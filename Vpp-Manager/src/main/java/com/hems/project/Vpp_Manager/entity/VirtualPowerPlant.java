package com.hems.project.Vpp_Manager.entity;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "vitual-power-plant")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VirtualPowerPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vpp-name")
    private String vppName;
    @Column(name = "max-power-generation-capacity")
    private Long maxPowerGenerationCpacity;

    private Geometry fretr;
    rfe
    
}
