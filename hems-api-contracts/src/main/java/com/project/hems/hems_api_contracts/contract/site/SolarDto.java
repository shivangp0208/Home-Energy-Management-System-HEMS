package com.project.hems.hems_api_contracts.contract.site;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@ToString
public class SolarDto {

    private UUID id;

    @Positive(message = "Total panel capacity must be greater than 0")
    private double totalPanelCapacityW;

    @Positive(message = "Inverter max capacity must be greater than 0")
    private double inverterMaxCapacityW;

    @NotBlank(message = "Orientation is required")
    @Size(max = 20, message = "Orientation must not exceed 20 characters")
    private String orientation;

    @JsonIgnore
    private SiteDto site;
}
