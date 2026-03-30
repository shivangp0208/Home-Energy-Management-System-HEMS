package com.project.hems.Payment_Service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyBalanceDto {
    
    @JsonProperty("site_id")
    private Long siteId;
    
    @JsonProperty("owner_id")
    private Long ownerId;
    
    // Consumption side
    @JsonProperty("home_usage_kwh")
    private BigDecimal homeUsageKwh;
    
    @JsonProperty("grid_import_kwh")
    private BigDecimal gridImportKwh;
    
    @JsonProperty("battery_discharge_kwh")
    private BigDecimal batteryDischargeKwh;
    
    // Production side
    @JsonProperty("solar_yield_kwh")
    private BigDecimal solarYieldKwh;
    
    @JsonProperty("battery_charge_kwh")
    private BigDecimal batteryChargeKwh;
    
    @JsonProperty("grid_export_kwh")
    private BigDecimal gridExportKwh;
    
    // Self-consumption percentage
    @JsonProperty("self_consumption_percent")
    private BigDecimal selfConsumptionPercent; // solarYield / homeUsage
    
    @JsonProperty("self_sufficiency_percent")
    private BigDecimal selfSufficiencyPercent; // (solarYield - gridExport) / homeUsage
}
