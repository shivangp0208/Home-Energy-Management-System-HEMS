package com.project.hems.hems_api_contracts.contract.dispatch;

import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.vpp.DispatchMode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceCommand {
    private UUID eventId; // For tracking
    private UUID siteId; // The specific site
    private UUID programId;
    private DispatchMode mode;
    private Integer durationMinutes;
    private Long targetPowerW;
    private Integer targetSoc;
}
