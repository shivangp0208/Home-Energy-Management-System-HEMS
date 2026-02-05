package com.hems.project.Virtual_Power_Plant.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;


@Data
public class Vpp {

    private UUID id;
    private String name;
    private String region;

    private Double totalSolarCapacityW;
    private Double totalBatteryCapacityWh;
    private Double availableBatteryCapacityWh;

    private Double currentLiveGeneratePowerW;

    private Double maxExportPowerW;
    private Double maxImportPowerW;

    private Integer totalSites;

    private String status;

    private LocalDateTime establishedTime;
    private LocalDateTime lastUpdatedTime;
}
