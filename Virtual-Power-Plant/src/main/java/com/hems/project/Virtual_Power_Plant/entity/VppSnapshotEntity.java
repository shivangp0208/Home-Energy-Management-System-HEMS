package com.hems.project.Virtual_Power_Plant.entity;

import com.project.hems.hems_api_contracts.contract.vpp.GenerationMode;
import com.project.hems.hems_api_contracts.contract.vpp.VppStrategyMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "strategy_mode")
    private VppStrategyMode strategyMode;

    @Column(name = "site_demand_w")
    private double siteDemandW;

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

    // Dispatch / Control
    private double targetExportW;

    @Enumerated(EnumType.STRING)
    private GenerationMode mode;

    // Capacity reference
    private double maxSolarCapacityW;
    private double maxCoalCapacityW;
    private double maxNuclearCapacityW;
    private double maxThermalCapacityW;

    // Accumulators
    private double totalGeneratedKwh;
    private double totalExportKwh;
    private double totalImportKwh;

    // Flags
    private boolean autoMode;
}
