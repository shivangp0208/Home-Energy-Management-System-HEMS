package com.project.hems.hems_api_contracts.contract.site;


import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SiteResponseDto {
    private UUID siteId;
    private OwnerDto owner;
    private boolean isActive;
    private List<SolarDto> solars;
    private BatteryDto batteryInfo;
    private AddressDto addressInfo;
    private List<UUID> programId;
}
