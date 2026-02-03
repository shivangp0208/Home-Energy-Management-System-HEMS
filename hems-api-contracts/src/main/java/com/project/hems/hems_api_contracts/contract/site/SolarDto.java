package com.project.hems.hems_api_contracts.contract.site;

import lombok.Data;

import java.util.UUID;
@Data
public class SolarDto {
    private UUID id;
    private double totalPanelCapacity;
    private double inverterMaxCapacity;
    private String orientation;
    private UUID siteId;
}

