package com.project.hems.hems_api_contracts.contract.envoy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchCommand {

    @NotNull
    private UUID dispatchId;

    @NotNull
    private UUID siteId;

    @NotNull
    private Long meterId;

    @NotNull
    private Instant timestamp;

    @NotNull
    private Instant validUntil;

    @NotEmpty
    private List<EnergyPriority> energyPriority;

    @NotNull
    private BatteryControl batteryControl;

    @NotNull
    private GridControl gridControl;

    private String reason;
}
