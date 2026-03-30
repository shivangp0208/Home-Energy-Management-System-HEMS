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
@Table(name = "energy_rates", indexes = {
    @Index(name = "idx_active_rate", columnList = "is_active")
})
public class EnergyRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "grid_buy_rate")
    private BigDecimal gridBuyRate; // $/kWh - what user pays for grid imports
    
    @Column(nullable = false, name = "solar_sell_rate")
    private BigDecimal solarSellRate; // $/kWh - what user earns for solar exports
    
    @Column(nullable = false, name = "fixed_monthly_charge")
    private BigDecimal fixedMonthlyCharge; // Fixed infrastructure cost
    
    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;
    
    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (effectiveFrom == null) {
            effectiveFrom = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
