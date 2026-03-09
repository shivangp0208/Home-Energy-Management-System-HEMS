package com.project.hems.hems_api_contracts.contract.envoy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.vpp.DispatchMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActiveControlState {
    private UUID eventId;

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

    private Instant validUntil;

    public boolean isActive(Instant now) {
        // If validUntil is null, treat it as "runs forever until stopped manually"
        // Otherwise, check if current time is before expiry
        if (validUntil == null)
            return true;
        return now.isBefore(validUntil);
    }
}
