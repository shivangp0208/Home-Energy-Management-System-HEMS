package com.project.hems.simulator_service.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.envoy.BatteryControl;
import com.project.hems.hems_api_contracts.contract.envoy.GridControl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActiveControlState {

    private UUID dispatchId;
    private BatteryControl batteryControl;
    private GridControl gridControl;
    private List<EnergyPriority> energyPriorities;

    private Instant validUntil;

    //TODO:-
    //move this active state in envoy service
    public boolean isActive(Instant now) {
        return validUntil != null && now.isBefore(validUntil);
    }
}
