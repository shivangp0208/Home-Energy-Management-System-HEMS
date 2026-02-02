package com.project.hems.envoy_manager_service.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteControlCommand {

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