package com.project.hems.SiteManagerService.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BatteryDto {
    private UUID id;
    private int quantity;
    private double capacityWh;
    private double maxOutputW;
    private UUID siteId;
}
