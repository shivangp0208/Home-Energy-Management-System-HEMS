package com.hems.project.Virtual_Power_Plant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VppUpdateResponseDto {
    private UUID id;
    private String name;
    private String region;

    private String country;

    private String city;


    private Double totalSolarCapacityW;
    private Double totalBatteryCapacityWh;
    private Double availableBatteryCapacityWh;

    private Double currentLiveGeneratePowerW;

    private Double maxExportPowerCapacityW;
    private Double maxImportPowerCapacityW;
    private Double maxPowerGenerationCapacityW;

    private Integer totalSites;


    private VppOperationalStatus operationalStatus;

    private LocalDateTime establishedTime;

    private Map<String, List<UUID>> siteCollection;

}
