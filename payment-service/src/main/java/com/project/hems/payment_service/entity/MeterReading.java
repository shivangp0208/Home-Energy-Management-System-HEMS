package com.project.hems.payment_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meter_readings", indexes = {
    @Index(name = "idx_site_date", columnList = "site_id,reading_date"),
    @Index(name = "idx_reading_date", columnList = "reading_date")
})
public class MeterReading {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "site_id")
    private Long siteId;
    
    @Column(nullable = false, name = "owner_id")
    private Long ownerId;
    
    @Column(nullable = false, name = "reading_date")
    private LocalDate readingDate; // Day of the reading
    
    @Column(nullable = false, name = "solar_yield_kwh")
    private BigDecimal solarYieldKwh; // Total solar energy produced that day
    
    @Column(nullable = false, name = "grid_import_kwh")
    private BigDecimal gridImportKwh; // Energy consumed from grid
    
    @Column(nullable = false, name = "grid_export_kwh")
    private BigDecimal gridExportKwh; // Energy sold back to grid
    
    @Column(nullable = false, name = "home_usage_kwh")
    private BigDecimal homeUsageKwh; // Total home consumption
    
    @Column(nullable = false, name = "battery_charge_kwh")
    private BigDecimal batteryChargeKwh; // Energy charged to battery
    
    @Column(nullable = false, name = "battery_discharge_kwh")
    private BigDecimal batteryDischargeKwh; // Energy from battery discharge
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "processed")
    private Boolean processed = false; // Whether this reading has been billed
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
