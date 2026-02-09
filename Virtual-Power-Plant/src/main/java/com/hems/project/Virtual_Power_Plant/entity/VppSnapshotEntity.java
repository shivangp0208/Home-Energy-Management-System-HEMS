package com.hems.project.Virtual_Power_Plant.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.vpp.GenerationMode;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vpp_snapshot")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VppSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vpp_id", nullable = false)
    private UUID vppId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Generation breakdown
    private double solarW;
    private double coalW;
    private double nuclearW;
    private double thermalW;

    private double totalGenerationW;

    // Battery + Grid
    private double batteryPowerW;
    private double gridPowerW;

    // Battery state
    private double batteryCapacityWh;
    private double batteryRemainingWh;
    private int batterySoc;

    // Dispatch target
    private double targetExportW;

    @Enumerated(EnumType.STRING)
    private GenerationMode mode;
}
