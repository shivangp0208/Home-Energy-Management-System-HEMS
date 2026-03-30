package com.project.hems.Payment_Service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_billing", indexes = {
    @Index(name = "idx_owner_month", columnList = "owner_id,billing_month"),
    @Index(name = "idx_billing_month", columnList = "billing_month")
})
public class UserBilling {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "owner_id")
    private Long ownerId;
    
    @Column(nullable = false, name = "site_id")
    private Long siteId;
    
    @Column(nullable = false, name = "billing_month")
    private YearMonth billingMonth; // e.g., 2024-03
    
    @Column(nullable = false, name = "total_solar_yield_kwh")
    private BigDecimal totalSolarYieldKwh;
    
    @Column(nullable = false, name = "total_grid_import_kwh")
    private BigDecimal totalGridImportKwh;
    
    @Column(nullable = false, name = "total_grid_export_kwh")
    private BigDecimal totalGridExportKwh;
    
    @Column(nullable = false, name = "total_home_usage_kwh")
    private BigDecimal totalHomeUsageKwh;
    
    @Column(nullable = false, name = "grid_charges")
    private BigDecimal gridCharges; // gridImportKwh × gridBuyRate
    
    @Column(nullable = false, name = "solar_earnings")
    private BigDecimal solarEarnings; // gridExportKwh × solarSellRate
    
    @Column(nullable = false, name = "fixed_charges")
    private BigDecimal fixedCharges; // Fixed monthly infrastructure cost
    
    @Column(nullable = false, name = "net_balance")
    private BigDecimal netBalance; // solarEarnings - gridCharges - fixedCharges
    
    @Column(name = "balance_status")
    @Enumerated(EnumType.STRING)
    private BalanceStatus balanceStatus; // CREDIT or DEBIT
    
    @Column(name = "invoice_generated")
    private Boolean invoiceGenerated = false;
    
    @Column(name = "invoice_sent")
    private Boolean invoiceSent = false;
    
    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Determine balance status
        if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
            balanceStatus = BalanceStatus.CREDIT; // User has credit/earnings
        } else {
            balanceStatus = BalanceStatus.DEBIT; // User owes money
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum BalanceStatus {
        CREDIT,  // User earned more than charges
        DEBIT    // User owes money
    }
    
    public enum PaymentStatus {
        PENDING,
        PAID,
        PARTIALLY_PAID,
        OVERDUE,
        CANCELLED
    }
}
