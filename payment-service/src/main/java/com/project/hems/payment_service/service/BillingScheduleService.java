package com.project.hems.Payment_Service.service;


import com.project.hems.Payment_Service.entity.MeterReading;
import com.project.hems.Payment_Service.repository.MeterReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class BillingScheduleService {
    
    private final MeterReadingRepository meterReadingRepository;
    private final BillingService billingService;
    
    /**
     * Scheduled task to process monthly billing
     * Runs at midnight on the 1st of each month
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void processMonthlyBilling() {
        log.info("Starting monthly billing process...");
        
        try {
            // Get previous month
            YearMonth previousMonth = YearMonth.now().minusMonths(1);
            
            // Get all unprocessed readings from the previous month
            List<MeterReading> unprocessedReadings = meterReadingRepository.findUnprocessedReadings();
            
            // Group readings by (owner, site)
            Map<String, Map<String, Object>> billingMap = new HashMap<>();
            
            for (MeterReading reading : unprocessedReadings) {
                // Only process readings from previous month
                if (!YearMonth.from(reading.getReadingDate()).equals(previousMonth)) {
                    continue;
                }
                
                String key = reading.getOwnerId() + "-" + reading.getSiteId();
                billingMap.putIfAbsent(key, new HashMap<>());
                
                Map<String, Object> billingData = billingMap.get(key);
                billingData.put("ownerId", reading.getOwnerId());
                billingData.put("siteId", reading.getSiteId());
            }
            
            // Process billing for each unique owner-site combination
            for (Map<String, Object> billingData : billingMap.values()) {
                try {
                    Long ownerId = (Long) billingData.get("ownerId");
                    Long siteId = (Long) billingData.get("siteId");
                    
                    log.info("Processing billing for owner: {}, site: {}, month: {}", 
                             ownerId, siteId, previousMonth);
                    
                    billingService.calculateMonthlyBilling(ownerId, siteId, previousMonth);
                    
                    log.info("Successfully processed billing for owner: {}, site: {}", 
                             ownerId, siteId);
                } catch (Exception e) {
                    log.error("Error processing billing", e);
                }
            }
            
            log.info("Monthly billing process completed");
        } catch (Exception e) {
            log.error("Error in monthly billing scheduler", e);
        }
    }
    
    /**
     * Process billing for a specific month (manual trigger)
     */
    @Transactional
    public void processMonthlyBillingForMonth(YearMonth month) {
        log.info("Processing billing for month: {}", month);
        
        List<MeterReading> readings = meterReadingRepository.findUnprocessedReadings();
        
        Map<String, Map<String, Object>> billingMap = new HashMap<>();
        
        for (MeterReading reading : readings) {
            if (!YearMonth.from(reading.getReadingDate()).equals(month)) {
                continue;
            }
            
            String key = reading.getOwnerId() + "-" + reading.getSiteId();
            billingMap.putIfAbsent(key, new HashMap<>());
            
            Map<String, Object> billingData = billingMap.get(key);
            billingData.put("ownerId", reading.getOwnerId());
            billingData.put("siteId", reading.getSiteId());
        }
        
        for (Map<String, Object> billingData : billingMap.values()) {
            try {
                Long ownerId = (Long) billingData.get("ownerId");
                Long siteId = (Long) billingData.get("siteId");
                
                billingService.calculateMonthlyBilling(ownerId, siteId, month);
            } catch (Exception e) {
                log.error("Error processing billing for owner: {}", 
                         billingData.get("ownerId"), e);
            }
        }
    }
}
