package com.project.hems.SiteManagerService.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SolarDto {
    private UUID id;
    private double totalPanelCapacityW;
    private double inverterMaxCapacityW;
    private String orientation;
    private UUID siteId;
}
