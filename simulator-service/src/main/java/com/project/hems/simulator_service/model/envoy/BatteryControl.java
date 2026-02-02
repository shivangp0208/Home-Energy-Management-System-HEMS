package com.project.hems.simulator_service.model.envoy;

import com.project.hems.simulator_service.model.BatteryMode;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class BatteryControl {
    private BatteryMode mode;
    private long targetPowerW;
    private long maxChargeW;
    private long maxDischargeW;
    private double minSocPercent;
    private double maxSocPercent;
}
