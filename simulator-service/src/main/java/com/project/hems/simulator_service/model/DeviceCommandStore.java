package com.project.hems.simulator_service.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.envoy.BatteryControl;
import com.project.hems.hems_api_contracts.contract.envoy.GridControl;
import com.project.hems.hems_api_contracts.contract.vpp.DispatchMode;

import lombok.Data;

@Component
@Data
public class DeviceCommandStore {

    private final ConcurrentHashMap<UUID, DeviceState> activeControls = new ConcurrentHashMap<>();

    public static final List<EnergyPriority> loadEnergyPriorities = List.of(EnergyPriority.SOLAR, EnergyPriority.GRID,
            EnergyPriority.BATTERY);
    public static final List<EnergyPriority> surplusEnergyPriorities = List.of(EnergyPriority.BATTERY,
            EnergyPriority.GRID);

    @Data
    public static class DeviceState {
        // THE ACTUAL COMMAND
        private DispatchMode mode;
        private Long targetPowerW;
        private Integer targetSoc;

        // THE SAFETY LIMITS (Never set these to null!)
        private BatteryControl batteryControl;
        private GridControl gridControl;

        // THE FALLBACK RULES
        private List<EnergyPriority> loadEnergyPriorities;
        private List<EnergyPriority> surplusEnergyPriorities;
    }

    public Optional<DeviceState> getEventState(UUID siteId) {
        if (activeControls.get(siteId) != null) {
            return Optional.of(activeControls.get(siteId));
        }

        return Optional.of(null);
    }
}
