package com.hems.project.Virtual_Power_Plant.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DispatchEventDto {

    private UUID eventId;

    @NotNull(message = "eventMode cannot be null")
    private DispatchMode eventMode;

    // If the mode is HOLD, power will be exactly 0. @Positive would block that.
    @NotNull(message = "targetPowerW cannot be null")
    @PositiveOrZero(message = "targetPowerW must be 0 or greater")
    private Long targetPowerW;

    // SoC can technically be 0% (empty), and it physically cannot exceed 100%.
    @NotNull(message = "targetSoc cannot be null")
    @Min(value = 0, message = "targetSoc cannot be less than 0%")
    @Max(value = 100, message = "targetSoc cannot be more than 100%")
    private Integer targetSoc;

    // If this is the payload sent to the Dispatch Service, it MUST contain the list
    // of sites.
    // Otherwise, the Dispatch Service won't know who to send the command to.
    @NotEmpty(message = "The list of valid sites cannot be empty")
    private List<UUID> validSiteIds;
}