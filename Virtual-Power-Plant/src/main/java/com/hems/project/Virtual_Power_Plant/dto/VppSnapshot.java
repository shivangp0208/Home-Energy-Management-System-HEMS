package com.hems.project.Virtual_Power_Plant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class VppSnapshot {

    private UUID vppId;
    private LocalDateTime timestamp;

    // ---------------- Generation Breakdown (Live Power W) ----------------
    private double solarW;
    private double coalW;
    private double nuclearW;
    private double thermalW;

    // Total live generation
    private double totalGenerationW;

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
