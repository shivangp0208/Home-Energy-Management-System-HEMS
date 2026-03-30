package com.project.hems.Payment_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingCalculationResponse {
    
    private Long siteId;
    private Long ownerId;
    
    // Energy metrics
    private BigDecimal totalSolarYieldKwh;
    private BigDecimal totalGridImportKwh;
    private BigDecimal totalGridExportKwh;
    private BigDecimal totalHomeUsageKwh;
    
    // Financial breakdown
    private BigDecimal gridCharges; // gridImportKwh × gridBuyRate
    private BigDecimal solarEarnings; // gridExportKwh × solarSellRate
    private BigDecimal fixedCharges; // Monthly infrastructure cost
    
    // Net balance
    private BigDecimal netBalance; // solarEarnings - gridCharges - fixedCharges
    private String balanceStatus; // "CREDIT" or "DEBIT"
    private String message; // User-friendly message
    
    // Rate information
    private BigDecimal gridBuyRate;
    private BigDecimal solarSellRate;
    private BigDecimal fixedMonthlyCharge;
}
