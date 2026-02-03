package com.project.hems.hems_api_contracts.contract.envoy;


import com.project.hems.hems_api_contracts.contract.simulator.BatteryMode;

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
