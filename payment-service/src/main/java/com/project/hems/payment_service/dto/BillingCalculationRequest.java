package com.project.hems.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingCalculationRequest {
    
    private Long siteId;
    private Long ownerId;
    
    private BigDecimal solarYieldKwh;
    private BigDecimal gridImportKwh;
    private BigDecimal gridExportKwh;
    private BigDecimal homeUsageKwh;
    private BigDecimal batteryChargeKwh;
    private BigDecimal batteryDischargeKwh;
    
    private BigDecimal gridBuyRate; // $/kWh
    private BigDecimal solarSellRate; // $/kWh
    private BigDecimal fixedMonthlyCharge; // Fixed cost
}
