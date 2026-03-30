package com.project.hems.payment_service.service;

import com.project.hems.payment_service.dto.BillingCalculationRequest;
import com.project.hems.payment_service.dto.BillingCalculationResponse;
import com.project.hems.payment_service.entity.*;
import com.project.hems.payment_service.repository.MeterReadingRepository;
import com.project.hems.payment_service.repository.UserBillingRepository;
import com.project.hems.payment_service.repository.TransactionRepository;
import com.project.hems.payment_service.repository.EnergyRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {
    
    private final MeterReadingRepository meterReadingRepository;
    private final UserBillingRepository userBillingRepository;
    private final TransactionRepository transactionRepository;
    private final EnergyRateRepository energyRateRepository;
    private final TransactionService transactionService;
    
    /**
     * Calculate billing for a single day's meter reading
     */
    @Transactional
    public BillingCalculationResponse calculateDailyBilling(BillingCalculationRequest request) {
        log.info("Calculating daily billing for site: {} owner: {}", 
                 request.getSiteId(), request.getOwnerId());
        
        // Get rates
        EnergyRate rate = energyRateRepository.findLatestActiveRate()
            .orElseThrow(() -> new RuntimeException("No active energy rate configured"));
        
        // Use request rates if provided, otherwise use configured rates
        BigDecimal gridBuyRate = request.getGridBuyRate() != null 
            ? request.getGridBuyRate() : rate.getGridBuyRate();
        BigDecimal solarSellRate = request.getSolarSellRate() != null 
            ? request.getSolarSellRate() : rate.getSolarSellRate();
        
        // Calculate charges and earnings
        BigDecimal gridCharges = request.getGridImportKwh()
            .multiply(gridBuyRate)
            .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal solarEarnings = request.getGridExportKwh()
            .multiply(solarSellRate)
            .setScale(2, RoundingMode.HALF_UP);
        
        // Fixed charge is applied monthly, not daily
        BigDecimal fixedCharges = BigDecimal.ZERO;
        
        // Calculate net balance
        BigDecimal netBalance = solarEarnings
            .subtract(gridCharges)
            .setScale(2, RoundingMode.HALF_UP);
        
        String message = formatBillingMessage(request, gridCharges, solarEarnings, netBalance);
        
        return BillingCalculationResponse.builder()
            .siteId(request.getSiteId())
            .ownerId(request.getOwnerId())
            .totalSolarYieldKwh(request.getSolarYieldKwh())
            .totalGridImportKwh(request.getGridImportKwh())
            .totalGridExportKwh(request.getGridExportKwh())
            .totalHomeUsageKwh(request.getHomeUsageKwh())
            .gridCharges(gridCharges)
            .solarEarnings(solarEarnings)
            .fixedCharges(fixedCharges)
            .netBalance(netBalance)
            .balanceStatus(netBalance.compareTo(BigDecimal.ZERO) >= 0 ? "CREDIT" : "DEBIT")
            .message(message)
            .gridBuyRate(gridBuyRate)
            .solarSellRate(solarSellRate)
            .fixedMonthlyCharge(rate.getFixedMonthlyCharge())
            .build();
    }
    
    /**
     * Calculate monthly billing by aggregating daily readings
     */
    @Transactional
    public BillingCalculationResponse calculateMonthlyBilling(Long ownerId, Long siteId, YearMonth month) {
        log.info("Calculating monthly billing for owner: {} site: {} month: {}", 
                 ownerId, siteId, month);
        
        // Get daily readings for the month
        List<MeterReading> readings = meterReadingRepository
            .findByMonthAndSite(siteId, month.getYear(), month.getMonthValue());
        
        if (readings.isEmpty()) {
            throw new RuntimeException("No meter readings found for month: " + month);
        }
        
        // Get rates
        EnergyRate rate = energyRateRepository.findLatestActiveRate()
            .orElseThrow(() -> new RuntimeException("No active energy rate configured"));
        
        // Aggregate all daily readings
        BigDecimal totalSolarYield = readings.stream()
            .map(MeterReading::getSolarYieldKwh)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalGridImport = readings.stream()
            .map(MeterReading::getGridImportKwh)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalGridExport = readings.stream()
            .map(MeterReading::getGridExportKwh)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalHomeUsage = readings.stream()
            .map(MeterReading::getHomeUsageKwh)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate charges and earnings with rates
        BigDecimal gridCharges = totalGridImport
            .multiply(rate.getGridBuyRate())
            .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal solarEarnings = totalGridExport
            .multiply(rate.getSolarSellRate())
            .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal fixedCharges = rate.getFixedMonthlyCharge();
        
        // Calculate net balance
        BigDecimal netBalance = solarEarnings
            .subtract(gridCharges)
            .subtract(fixedCharges)
            .setScale(2, RoundingMode.HALF_UP);
        
        String message = formatMonthlyBillingMessage(
            totalSolarYield, totalGridImport, totalGridExport, 
            gridCharges, solarEarnings, fixedCharges, netBalance);
        
        // Create UserBilling record
        UserBilling userBilling = new UserBilling();
        userBilling.setOwnerId(ownerId);
        userBilling.setSiteId(siteId);
        userBilling.setBillingMonth(month);
        userBilling.setTotalSolarYieldKwh(totalSolarYield);
        userBilling.setTotalGridImportKwh(totalGridImport);
        userBilling.setTotalGridExportKwh(totalGridExport);
        userBilling.setTotalHomeUsageKwh(totalHomeUsage);
        userBilling.setGridCharges(gridCharges);
        userBilling.setSolarEarnings(solarEarnings);
        userBilling.setFixedCharges(fixedCharges);
        userBilling.setNetBalance(netBalance);
        
        UserBilling savedBilling = userBillingRepository.save(userBilling);
        
        // Record transactions
        recordMonthlyTransactions(savedBilling, rate);
        
        // Mark readings as processed
        readings.forEach(r -> {
            r.setProcessed(true);
            meterReadingRepository.save(r);
        });
        
        return BillingCalculationResponse.builder()
            .siteId(siteId)
            .ownerId(ownerId)
            .totalSolarYieldKwh(totalSolarYield)
            .totalGridImportKwh(totalGridImport)
            .totalGridExportKwh(totalGridExport)
            .totalHomeUsageKwh(totalHomeUsage)
            .gridCharges(gridCharges)
            .solarEarnings(solarEarnings)
            .fixedCharges(fixedCharges)
            .netBalance(netBalance)
            .balanceStatus(netBalance.compareTo(BigDecimal.ZERO) > 0 ? "CREDIT" : "DEBIT")
            .message(message)
            .gridBuyRate(rate.getGridBuyRate())
            .solarSellRate(rate.getSolarSellRate())
            .fixedMonthlyCharge(rate.getFixedMonthlyCharge())
            .build();
    }
    
    /**
     * Record monthly transactions in the transaction log
     */
    private void recordMonthlyTransactions(UserBilling billing, EnergyRate rate) {
        // Record solar earnings transaction
        if (billing.getSolarEarnings().compareTo(BigDecimal.ZERO) > 0) {
            Transaction solarTx = new Transaction();
            solarTx.setOwnerId(billing.getOwnerId());
            solarTx.setSiteId(billing.getSiteId());
            solarTx.setBillingId(billing.getId());
            solarTx.setTransactionType(Transaction.TransactionType.SOLAR_EARNING);
            solarTx.setAmount(billing.getSolarEarnings());
            solarTx.setDescription(String.format("Solar export %.2f kWh @ $%.2f/kWh", 
                billing.getTotalGridExportKwh(), rate.getSolarSellRate()));
            solarTx.setReferenceNumber("SOLAR-" + billing.getId());
            transactionRepository.save(solarTx);
        }
        
        // Record grid charges transaction
        if (billing.getGridCharges().compareTo(BigDecimal.ZERO) > 0) {
            Transaction gridTx = new Transaction();
            gridTx.setOwnerId(billing.getOwnerId());
            gridTx.setSiteId(billing.getSiteId());
            gridTx.setBillingId(billing.getId());
            gridTx.setTransactionType(Transaction.TransactionType.GRID_CHARGE);
            gridTx.setAmount(billing.getGridCharges().negate()); // Negative for charges
            gridTx.setDescription(String.format("Grid import %.2f kWh @ $%.2f/kWh", 
                billing.getTotalGridImportKwh(), rate.getGridBuyRate()));
            gridTx.setReferenceNumber("GRID-" + billing.getId());
            transactionRepository.save(gridTx);
        }
        
        // Record fixed charges transaction
        if (billing.getFixedCharges().compareTo(BigDecimal.ZERO) > 0) {
            Transaction fixedTx = new Transaction();
            fixedTx.setOwnerId(billing.getOwnerId());
            fixedTx.setSiteId(billing.getSiteId());
            fixedTx.setBillingId(billing.getId());
            fixedTx.setTransactionType(Transaction.TransactionType.FIXED_CHARGE);
            fixedTx.setAmount(billing.getFixedCharges().negate()); // Negative for charges
            fixedTx.setDescription("Monthly infrastructure and meter rental charge");
            fixedTx.setReferenceNumber("FIXED-" + billing.getId());
            transactionRepository.save(fixedTx);
        }
    }
    
    private String formatBillingMessage(BillingCalculationRequest request, 
                                       BigDecimal gridCharges, 
                                       BigDecimal solarEarnings, 
                                       BigDecimal netBalance) {
        return String.format(
            "Daily Summary: Solar Export: %.2f kWh (Earn: $%.2f), Grid Import: %.2f kWh (Pay: $%.2f), Net: $%.2f",
            request.getGridExportKwh(), solarEarnings,
            request.getGridImportKwh(), gridCharges,
            netBalance
        );
    }
    
    private String formatMonthlyBillingMessage(BigDecimal solarYield, BigDecimal gridImport, 
                                              BigDecimal gridExport, BigDecimal gridCharges, 
                                              BigDecimal solarEarnings, BigDecimal fixedCharges, 
                                              BigDecimal netBalance) {
        String status = netBalance.compareTo(BigDecimal.ZERO) > 0 
            ? String.format("CREDIT: You earned $%.2f", netBalance)
            : String.format("DEBIT: You owe $%.2f", netBalance.abs());
        
        return String.format(
            "Monthly Summary: Solar Yield: %.2f kWh, Grid Export: %.2f kWh (Earn: $%.2f), " +
            "Grid Import: %.2f kWh (Pay: $%.2f), Fixed Cost: $%.2f, %s",
            solarYield, gridExport, solarEarnings,
            gridImport, gridCharges, fixedCharges, status
        );
    }
}
