package com.hems.project.Virtual_Power_Plant.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class VppSimConfig {

    private UUID vppId;

    // Max capacity limits (W)
    private double maxSolarW;
    private double maxCoalW;
    private double maxNuclearW;
    private double maxThermalW;

    // Battery
    private double batteryCapacityWh;
    private double initialBatteryWh;

    // Grid limits
    private double maxExportW;
    private double maxImportW;
}
