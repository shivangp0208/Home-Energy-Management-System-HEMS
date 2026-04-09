package com.project.hems.payment_service.service;


import com.project.hems.payment_service.entity.EnergyRate;
import com.project.hems.payment_service.repository.EnergyRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyRateService {
    
    private final EnergyRateRepository energyRateRepository;
    
    /**
     * Get the currently active energy rate
     */
    public EnergyRate getCurrentRate() {
        return energyRateRepository.findLatestActiveRate()
            .orElseThrow(() -> new RuntimeException("No active energy rate configured"));
    }
    
    /**
     * Get the active rate at a specific point in time
     */
    public EnergyRate getRateAtTime(LocalDateTime dateTime) {
        return energyRateRepository.findActiveRateAtTime(dateTime)
            .orElseThrow(() -> new RuntimeException("No active rate found for date: " + dateTime));
    }
    
    /**
     * Create or update an energy rate
     */
    @Transactional
    public EnergyRate createOrUpdateRate(EnergyRate rate) {
        log.info("Creating/updating energy rate: Grid Buy: ${}, Solar Sell: ${}, Fixed: ${}",
                 rate.getGridBuyRate(), rate.getSolarSellRate(), rate.getFixedMonthlyCharge());
        
        // If this is an update (has ID), deactivate old rate
        if (rate.getId() != null) {
            EnergyRate existing = energyRateRepository.findById(rate.getId())
                .orElseThrow(() -> new RuntimeException("Rate not found"));
            existing.setIsActive(false);
            existing.setEffectiveTo(LocalDateTime.now());
            energyRateRepository.save(existing);
        }
        
        // Set new rate as active
        rate.setIsActive(true);
        if (rate.getEffectiveFrom() == null) {
            rate.setEffectiveFrom(LocalDateTime.now());
        }
        
        return energyRateRepository.save(rate);
    }
    
    /**
     * Deactivate a rate
     */
    @Transactional
    public void deactivateRate(Long rateId) {
        EnergyRate rate = energyRateRepository.findById(rateId)
            .orElseThrow(() -> new RuntimeException("Rate not found"));
        rate.setIsActive(false);
        rate.setEffectiveTo(LocalDateTime.now());
        energyRateRepository.save(rate);
        log.info("Rate deactivated: {}", rateId);
    }
    
    /**
     * Get all active rates (historical)
     */
    public List<EnergyRate> getAllActiveRates() {
        return energyRateRepository.findAllActiveRates();
    }
}
