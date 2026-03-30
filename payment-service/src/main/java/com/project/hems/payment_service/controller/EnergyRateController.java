package com.project.hems.Payment_Service.controller;

import com.project.hems.Payment_Service.entity.EnergyRate;
import com.project.hems.Payment_Service.service.EnergyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
public class EnergyRateController {
    
    private final EnergyRateService energyRateService;
    
    /**
     * Get the currently active energy rate
     */
    @GetMapping("/current")
    public ResponseEntity<EnergyRate> getCurrentRate() {
        log.info("Fetching current energy rate");
        
        EnergyRate rate = energyRateService.getCurrentRate();
        return ResponseEntity.ok(rate);
    }
    
    /**
     * Get all active rates (historical)
     */
    @GetMapping("/all")
    public ResponseEntity<List<EnergyRate>> getAllActiveRates() {
        log.info("Fetching all active energy rates");
        
        List<EnergyRate> rates = energyRateService.getAllActiveRates();
        return ResponseEntity.ok(rates);
    }
    
    /**
     * Create a new energy rate
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createRate(@RequestBody EnergyRate rate) {
        log.info("Creating new energy rate - Grid Buy: ${}, Solar Sell: ${}, Fixed: ${}",
                 rate.getGridBuyRate(), rate.getSolarSellRate(), rate.getFixedMonthlyCharge());
        
        EnergyRate savedRate = energyRateService.createOrUpdateRate(rate);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", "Energy rate created successfully",
            "rateId", savedRate.getId(),
            "gridBuyRate", savedRate.getGridBuyRate(),
            "solarSellRate", savedRate.getSolarSellRate(),
            "fixedMonthlyCharge", savedRate.getFixedMonthlyCharge(),
            "effectiveFrom", savedRate.getEffectiveFrom()
        ));
    }
    
    /**
     * Update an existing energy rate
     */
    @PutMapping("/update/{rateId}")
    public ResponseEntity<Map<String, Object>> updateRate(
            @PathVariable Long rateId,
            @RequestBody EnergyRate rate) {
        log.info("Updating energy rate: {}", rateId);
        
        rate.setId(rateId);
        EnergyRate updatedRate = energyRateService.createOrUpdateRate(rate);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Energy rate updated successfully",
            "rateId", updatedRate.getId(),
            "gridBuyRate", updatedRate.getGridBuyRate(),
            "solarSellRate", updatedRate.getSolarSellRate(),
            "fixedMonthlyCharge", updatedRate.getFixedMonthlyCharge(),
            "effectiveFrom", updatedRate.getEffectiveFrom()
        ));
    }
    
    /**
     * Deactivate a rate
     */
    @DeleteMapping("/deactivate/{rateId}")
    public ResponseEntity<Map<String, Object>> deactivateRate(@PathVariable Long rateId) {
        log.info("Deactivating energy rate: {}", rateId);
        
        energyRateService.deactivateRate(rateId);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Energy rate deactivated successfully"
        ));
    }
}
