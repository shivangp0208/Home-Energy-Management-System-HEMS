package com.project.hems.hems_api_contracts.contract.site;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.project.hems.hems_api_contracts.contract.program.Program;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
// @JsonFilter("siteFilter") //dynamic filter we make for filter out some filed in response
public class SiteDto {

    private UUID siteId;

    @NotNull(message = "OwnerId is required")
    private UUID ownerId;

    private boolean siteStatus;

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
