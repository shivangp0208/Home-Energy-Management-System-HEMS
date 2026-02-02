package com.project.hems.envoy_manager_service.model;

import com.project.hems.envoy_manager_service.model.simulator.BatteryMode;

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
public class BatteryControl {
    private BatteryMode mode;
    private long targetPowerW;
    private long maxChargeW;
    private long maxDischargeW;
    private double minSocPercent;
    private double maxSocPercent;
}
