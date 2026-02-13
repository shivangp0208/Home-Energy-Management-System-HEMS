package com.project.hems.hems_api_contracts.contract.site;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Positive;

@Data
@ToString
public class BatteryDto {

    private UUID batteryId;

    @Positive(message = "Battery capacity (Wh) must be greater than 0")
    private double capacityWh;

    @Positive(message = "Battery max output (W) must be greater than 0")
    private double maxOutputW;

    @JsonIgnore
    private SiteDto site;
}
