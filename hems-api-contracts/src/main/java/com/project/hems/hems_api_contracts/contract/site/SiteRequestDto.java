package com.project.hems.hems_api_contracts.contract.site;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SiteRequestDto {
    private UUID ownerId;
    private boolean isActive;
    private List<SolarDto> solars;
    private BatteryDto battery;
    private AddressDto address;
    private List<UUID> programId;//jema user e ui mathi select karyu hase
}
