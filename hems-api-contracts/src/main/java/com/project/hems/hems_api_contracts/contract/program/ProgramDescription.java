package com.project.hems.hems_api_contracts.contract.program;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.envoy.BatteryControl;
import com.project.hems.hems_api_contracts.contract.envoy.GridControl;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProgramDescription {

    private Long descriptionId;

    @JsonIgnore
    private Program program;

    @NotNull(message = "load energy order cannot be null")
    @Size(min = 1, message = "atleast one energy priority should be there in energy order")
    private List<EnergyPriority> loadEnergyOrder;
    
    @NotNull(message = "surplus energy order cannot be null")
    @Size(min = 1, message = "atleast one energy priority should be there in energy order")
    private List<EnergyPriority> surplusEnergyOrder;
    
    @NotNull(message = "grid control cannot be null")
    private GridControl gridControl;

    @NotNull(message = "battery control cannot be null")
    private BatteryControl batteryControl;

}
