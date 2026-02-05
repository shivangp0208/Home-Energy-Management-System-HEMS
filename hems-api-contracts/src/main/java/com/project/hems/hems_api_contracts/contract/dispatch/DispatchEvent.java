package com.project.hems.hems_api_contracts.contract.dispatch;

import java.util.List;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@Builder
public class DispatchEvent {

    // @NotNull(message = "dispatchId cannot be null")
    private UUID dispatchId;

    @NotNull(message = "siteId cannot be null")
    private UUID siteId;

    @NotNull(message = "eventType cannot be null")
    private DispatchEventType eventType;

    @NotNull(message = "powerReqW cannot be null")
    @Positive(message = "powerReqW must be greater than 0")
    private long powerReqW;

    @NotNull(message = "durationSec cannot be null")
    @Positive(message = "durationSec must be greater than 0")
    private long durationSec;

    @NotEmpty(message = "energyPriority cannot be empty")
    private List<@NotNull EnergyPriority> energyPriority;

    @Size(min = 1, max = 255, message = "reason must be at most 255 characters")
    private String reason;


}
