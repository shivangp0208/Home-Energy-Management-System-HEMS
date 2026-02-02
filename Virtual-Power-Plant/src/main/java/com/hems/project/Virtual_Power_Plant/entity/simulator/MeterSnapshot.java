package com.hems.project.Virtual_Power_Plant.entity.simulator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MeterSnapshot  {

    private Long meterId;

    private UUID siteId;

    private LocalDateTime timestamp;

    private Double solarProductionW;

   
    private Double homeConsumptionW;

    private Double batteryPowerW; 

    private Double gridPowerW; 

    private Double totalSolarYieldKwh;

    private Double totalGridImportKwh;

    private Double totalGridExportKwh;

    private Double totalHomeUsageKwh;
    private Double batteryCapacityWh;

    private Double batteryRemainingWh;

    private ChargingStatus chargingStatus;

    private BatteryMode batteryMode;

    private Double currentVoltage;
    private Double currentAmps;

    private Integer batterySoc;

    private List<EnergyPriority> energyPriorities;
}