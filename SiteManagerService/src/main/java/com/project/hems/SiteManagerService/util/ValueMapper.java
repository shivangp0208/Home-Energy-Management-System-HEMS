package com.project.hems.SiteManagerService.util;

import com.project.hems.SiteManagerService.entity.*;
import com.project.hems.hems_api_contracts.contract.site.AddressDto;
import com.project.hems.hems_api_contracts.contract.site.BatteryDto;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.hems_api_contracts.contract.site.SiteResponseDto;
import com.project.hems.hems_api_contracts.contract.site.SolarDto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ValueMapper {

    @Autowired
    private ModelMapper modelMapper;

    public OwnerDto ownerModelToDto(Owner owner) {
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setId(owner.getId());
        ownerDto.setOwnerName(owner.getOwnerName());
        ownerDto.setEmail(owner.getEmail());
        ownerDto.setPhoneNo(owner.getPhoneNo());
        if (owner.getSites() != null) {
            // change karvu Long -> UUID
            List<UUID> sitesIds = new ArrayList<>();
            // owner jode site ni list hase toh ek ek laisu and ownerDto ma set karine
            // return karavsu
            owner.getSites().forEach(ownerSite -> {
                sitesIds.add(ownerSite.getId());
            });
            ownerDto.setSitesIds(sitesIds);
        }
        return ownerDto;
    }

    public Owner ownerDtoToModel(OwnerDto ownerDto, List<Site> site) {
        Owner owner = new Owner();
        owner.setId(ownerDto.getId());
        owner.setOwnerName(ownerDto.getOwnerName());
        owner.setEmail(ownerDto.getEmail());
        owner.setPhoneNo(ownerDto.getPhoneNo());
        owner.setSites(site);
        return owner;

    }

    public Address addressDtoToModel(AddressDto addressDto, Site site) {
        Address address = new Address();
        address.setId(addressDto.getId());
        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCounty(addressDto.getCounty());
        address.setSite(site);
        return address;
    }

    public BatteryDto batteryModelToDto(Battery battery) {
        BatteryDto batteryDto = new BatteryDto();
        batteryDto.setId(battery.getId());
        batteryDto.setQuantity(battery.getQuantity());
        batteryDto.setCapacityWh(battery.getCapacityWh());
        batteryDto.setMaxOutputW(battery.getMaxOutputW());
        if (battery.getSite() != null) {
            batteryDto.setSiteId(battery.getSite().getId());
        }
        return batteryDto;
    }

    public Battery batteryDtoToModel(BatteryDto batteryDto, Site site) {
        Battery battery = new Battery();
        battery.setId(batteryDto.getId());
        battery.setQuantity(batteryDto.getQuantity());
        battery.setCapacityWh(batteryDto.getCapacityWh());
        battery.setMaxOutputW(batteryDto.getMaxOutputW());
        battery.setSite(site);
        return battery;
    }

    public SolarDto solarModelToDto(Solar solar) {
        SolarDto solarDto = new SolarDto();
        solarDto.setId(solar.getId());
        solarDto.setTotalPanelCapacityW(solar.getTotalPanelCapacityW());
        solarDto.setInverterMaxCapacityW(solar.getInverterMaxCapacityW());
        solarDto.setOrientation(solar.getOrientation());
        if (solar.getSite() != null) {
            solarDto.setSiteId(solar.getSite().getId());
        }
        return solarDto;
    }

    public Solar solarDtoToModel(SolarDto solarDto, Site site) {
        Solar solar = new Solar();
        solar.setId(solarDto.getId());
        solar.setTotalPanelCapacityW(solarDto.getTotalPanelCapacityW());
        solar.setInverterMaxCapacityW(solarDto.getInverterMaxCapacityW());
        solar.setOrientation(solarDto.getOrientation());
        solar.setSite(site);
        return solar;
    }

    public SiteResponseDto siteModelToResponseDto(Site site) {
        SiteResponseDto dto = new SiteResponseDto();

        dto.setSiteId(site.getId());
        dto.setActive(site.isActive());

        if (site.getOwner() != null) {
            dto.setOwner(ownerModelToDto(site.getOwner()));
        }

        if (site.getSolar() != null) {
            List<SolarDto> solarDtos = site.getSolar()
                    .stream()
                    .map(this::solarModelToDto)
                    .toList();
            dto.setSolars(solarDtos);
        }

        if (site.getBattery() != null) {
            dto.setBatteryInfo(batteryModelToDto(site.getBattery()));
        }

        if (site.getAddress() != null) {
            Address address = site.getAddress();
            AddressDto addressDto = new AddressDto();
            addressDto.setId(address.getId());
            addressDto.setAddressLine1(address.getAddressLine1());
            addressDto.setAddressLine2(address.getAddressLine2());
            addressDto.setCity(address.getCity());
            addressDto.setState(address.getState());
            addressDto.setPostalCode(address.getPostalCode());
            addressDto.setCounty(address.getCounty());
            addressDto.setSiteId(site.getId());
            dto.setAddressInfo(addressDto);
        }

        return dto;
    }

}