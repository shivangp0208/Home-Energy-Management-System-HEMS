package com.project.hems.SiteManagerService.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.site.AddressDto;
import com.project.hems.hems_api_contracts.contract.site.BatteryDto;
import com.project.hems.hems_api_contracts.contract.site.SolarDto;

@Data
public class SiteRequestDto {
    private UUID ownerId;
    private boolean isActive;
    private List<SolarDto> solars;
    private BatteryDto battery;
    private AddressDto address;
//    private List<UUID> programId;// jema user e ui mathi select karyu hase
    //aa programId have apde jyare vpp enroll karse tyare ema add karsu
}
