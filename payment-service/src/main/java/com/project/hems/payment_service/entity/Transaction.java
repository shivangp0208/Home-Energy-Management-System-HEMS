package com.project.hems.Payment_Service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_owner_date", columnList = "owner_id,transaction_date"),
    @Index(name = "idx_transaction_type", columnList = "transaction_type"),
    @Index(name = "idx_billing_id", columnList = "billing_id")
})
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "owner_id")
    private Long ownerId;
    
    @Column(nullable = false, name = "site_id")
    private Long siteId;
    
    @Column(name = "billing_id")
    private Long billingId; // Reference to UserBilling record
    
    @Column(nullable = false, name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // SOLAR_EARNING, GRID_CHARGE, FIXED_CHARGE, PAYMENT
    
    @Column(nullable = false, name = "amount")
    private BigDecimal amount; // Amount in dollars
    
    @Column(name = "description")
    private String description; // e.g., "Solar export 50kWh @ $0.08/kWh"
    
    @Column(name = "reference_number")
    private String referenceNumber; // For payment tracking
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.COMPLETED;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }
    
    public enum TransactionType {
        SOLAR_EARNING,      // User earned from solar export
        GRID_CHARGE,        // User charged for grid import
        FIXED_CHARGE,       // Monthly infrastructure charge
        CREDIT_ADJUSTMENT,  // Manual adjustment/credit
        PAYMENT,            // User payment towards bill
        REFUND              // Refund to user
    }
    
    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REVERSED
    }
}
