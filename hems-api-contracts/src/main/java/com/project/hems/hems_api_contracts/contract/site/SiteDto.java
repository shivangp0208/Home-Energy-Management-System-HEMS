package com.project.hems.hems_api_contracts.contract.site;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.hems.hems_api_contracts.contract.program.Program;

@Data
@ToString
public class SiteDto {

    private UUID siteId;

    @NotNull(message = "Owner is required")
    private OwnerDto owner;

    @JsonProperty("isActive")
    private boolean isActive;

    @NotEmpty(message = "At least one solar configuration is required")
    @Valid
    private List<SolarDto> solar = new ArrayList<>();

    @NotEmpty(message = "At least one battery is required")
    @Valid
    private List<BatteryDto> batteries = new ArrayList<>();

    @NotNull(message = "Address is required")
    @Valid
    private AddressDto address;

    @ToString.Exclude
    private List<Program> enrollProgramIds = new ArrayList<>();
}
