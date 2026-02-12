package com.project.hems.hems_api_contracts.contract.vpp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class VppSnapshot {

    private UUID vppId;
    private LocalDateTime timestamp;


    private com.project.hems.hems_api_contracts.contract.vpp.VppStrategyMode strategyMode;
    private double siteDemandW;


    // ---------------- Generation Breakdown (Live Power W) ----------------
    private double solarW;
    private double coalW;
    private double nuclearW;
    private double thermalW;

    // Total live generation
    private double totalGenerationW;
    private boolean arbitrageEnabled;

    // ---------------- Battery + Grid ----------------
    private double batteryPowerW;   // + charge / - discharge
    private double gridPowerW;      // + import / - export

    // ---------------- Battery State ----------------
    private double batteryCapacityWh;
    private double batteryRemainingWh;
    private int batterySoc;

    // ---------------- Dispatch / Control ----------------
    private double targetExportW;   // VPP target export power
    private GenerationMode mode;    // AUTO / MANUAL / DISPATCH

    // ---------------- Capacity Reference (NEW - IMPORTANT) ----------------
    private double maxSolarCapacityW;
    private double maxCoalCapacityW;
    private double maxNuclearCapacityW;
    private double maxThermalCapacityW;

    // ---------------- Accumulators (NEW - FUTURE BILLING / REPORTING) -----
    private double totalGeneratedKwh;
    private double totalExportKwh;
    private double totalImportKwh;

    // ---------------- System Flags ----------------
    private boolean autoMode;
}
