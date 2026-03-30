package com.project.hems.Payment_Service.controller;

import com.project.hems.Payment_Service.dto.BillingCalculationRequest;
import com.project.hems.Payment_Service.dto.BillingCalculationResponse;
import com.project.hems.Payment_Service.entity.Transaction;
import com.project.hems.Payment_Service.entity.UserBilling;
import com.project.hems.Payment_Service.repository.TransactionRepository;
import com.project.hems.Payment_Service.repository.UserBillingRepository;
import com.project.hems.Payment_Service.service.BillingService;
import com.project.hems.Payment_Service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {
    
    private final BillingService billingService;
    private final TransactionService transactionService;
    private final UserBillingRepository userBillingRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Calculate daily billing for given meter reading
     */
    @PostMapping("/calculate-daily")
    public ResponseEntity<BillingCalculationResponse> calculateDailyBilling(
            @RequestBody BillingCalculationRequest request) {
        log.info("Calculating daily billing for site: {}", request.getSiteId());
        
        BillingCalculationResponse response = billingService.calculateDailyBilling(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Calculate monthly billing for an owner/site
     */
    @GetMapping("/calculate-monthly/{ownerId}/{siteId}/{yearMonth}")
    public ResponseEntity<BillingCalculationResponse> calculateMonthlyBilling(
            @PathVariable Long ownerId,
            @PathVariable Long siteId,
            @PathVariable String yearMonth) {
        log.info("Calculating monthly billing for owner: {}, site: {}, month: {}", 
                 ownerId, siteId, yearMonth);
        
        YearMonth month = YearMonth.parse(yearMonth);
        BillingCalculationResponse response = billingService.calculateMonthlyBilling(ownerId, siteId, month);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get billing history for an owner
     */
    @GetMapping("/history/owner/{ownerId}")
    public ResponseEntity<List<UserBilling>> getOwnerBillingHistory(
            @PathVariable Long ownerId) {
        log.info("Fetching billing history for owner: {}", ownerId);
        
        List<UserBilling> billings = userBillingRepository.findByOwnerOrderByMonthDesc(ownerId);
        return ResponseEntity.ok(billings);
    }
    
    /**
     * Get billing history for a site
     */
    @GetMapping("/history/site/{siteId}")
    public ResponseEntity<List<UserBilling>> getSiteBillingHistory(
            @PathVariable Long siteId) {
        log.info("Fetching billing history for site: {}", siteId);
        
        List<UserBilling> billings = userBillingRepository.findBySiteOrderByMonthDesc(siteId);
        return ResponseEntity.ok(billings);
    }
    
    /**
     * Get billing details for a specific month
     */
    @GetMapping("/details/{ownerId}/{siteId}/{yearMonth}")
    public ResponseEntity<UserBilling> getBillingDetails(
            @PathVariable Long ownerId,
            @PathVariable Long siteId,
            @PathVariable String yearMonth) {
        log.info("Fetching billing details for owner: {}, site: {}, month: {}", 
                 ownerId, siteId, yearMonth);
        
        YearMonth month = YearMonth.parse(yearMonth);
        UserBilling billing = userBillingRepository.findByOwnerAndMonth(ownerId, month)
            .orElseThrow(() -> new RuntimeException("Billing not found for month: " + month));
        
        return ResponseEntity.ok(billing);
    }
    
    /**
     * Get billing summary for owner (latest month)
     */
    @GetMapping("/summary/{ownerId}")
    public ResponseEntity<UserBilling> getBillingSummary(@PathVariable Long ownerId) {
        log.info("Fetching latest billing summary for owner: {}", ownerId);
        
        List<UserBilling> billings = userBillingRepository.findByOwnerOrderByMonthDesc(ownerId);
        if (billings.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(billings.get(0));
    }
    
    /**
     * Record a payment towards a bill
     */
    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> recordPayment(
            @RequestParam Long ownerId,
            @RequestParam Long siteId,
            @RequestParam Long billingId,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam(required = false) String referenceNumber) {
        log.info("Recording payment for owner: {}, site: {}, amount: ${}", 
                 ownerId, siteId, amount);
        
        if (referenceNumber == null || referenceNumber.isEmpty()) {
            referenceNumber = "PAY-" + System.currentTimeMillis();
        }
        
        Transaction payment = transactionService.recordPayment(
            ownerId, siteId, billingId, amount, referenceNumber);
        
        // Update billing payment status
        UserBilling billing = userBillingRepository.findById(billingId)
            .orElseThrow(() -> new RuntimeException("Billing not found"));
        billing.setPaymentStatus(UserBilling.PaymentStatus.PAID);
        userBillingRepository.save(billing);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Payment recorded successfully",
            "transactionId", payment.getId(),
            "referenceNumber", payment.getReferenceNumber()
        ));
    }
    
    /**
     * Get all pending payments
     */
    @GetMapping("/pending-payments")
    public ResponseEntity<List<UserBilling>> getPendingPayments() {
        log.info("Fetching all pending payments");

        List<UserBilling> pendingPayments =
                userBillingRepository.findByPaymentStatus(UserBilling.PaymentStatus.PENDING);

        return ResponseEntity.ok(pendingPayments);
    }
    
    /**
     * Get overdue payments
     */
    @GetMapping("/overdue-payments")
    public ResponseEntity<List<UserBilling>> getOverduePayments() {
        log.info("Fetching overdue payments");

        List<UserBilling> overduePayments =
                userBillingRepository.findByPaymentStatus(UserBilling.PaymentStatus.OVERDUE);

        return ResponseEntity.ok(overduePayments);
    }
}
