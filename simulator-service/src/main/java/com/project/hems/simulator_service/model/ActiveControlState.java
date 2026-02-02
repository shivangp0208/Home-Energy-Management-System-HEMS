package com.project.hems.simulator_service.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.project.hems.simulator_service.model.envoy.BatteryControl;
import com.project.hems.simulator_service.model.envoy.EnergyPriority;
import com.project.hems.simulator_service.model.envoy.GridControl;

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

    public boolean isActive(Instant now) {
        return validUntil != null && now.isBefore(validUntil);
    }
}
