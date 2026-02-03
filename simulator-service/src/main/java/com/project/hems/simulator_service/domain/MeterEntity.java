package com.project.hems.simulator_service.domain;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.simulator.BatteryMode;
import com.project.hems.hems_api_contracts.contract.simulator.ChargingStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meter_info")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MeterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull(message = "siteId cannot be null")
    private UUID siteId;

    // --- Cumulative Energy Accumulators (kWh) ---
    // Using precision (15,4) to prevent rounding errors in energy accounting

    @ElementCollection(targetClass = EnergyPriority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "energy_priority", joinColumns = @JoinColumn(name = "meter_id"))
    @Column(name = "energy_priority_id")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "energy priority field cannot be null")
    private List<EnergyPriority> energyPriorities;

    @Builder.Default
    @Column(precision = 15, scale = 4)
    @JdbcTypeCode(Types.DECIMAL)
    @NotNull
    @PositiveOrZero(message = "totalSolarYieldKwh cannot be negative")
    private double totalSolarYieldKwh = 0.0;

    @Builder.Default
    @Column(precision = 15, scale = 4)
    @JdbcTypeCode(Types.DECIMAL)
    @NotNull
    @PositiveOrZero(message = "totalGridImportKwh cannot be negative")
    private double totalGridImportKwh = 0.0;

    @Builder.Default
    @Column(precision = 15, scale = 4)
    @JdbcTypeCode(Types.DECIMAL)
    @NotNull
    @PositiveOrZero(message = "totalGridExportKwh cannot be negative")
    private double totalGridExportKwh = 0.0;

    @Builder.Default
    @Column(precision = 15, scale = 4)
    @JdbcTypeCode(Types.DECIMAL)
    @NotNull
    @PositiveOrZero(message = "totalHomeUsageKwh cannot be negative")
    private double totalHomeUsageKwh = 0.0;

    // --- Battery Configuration & State ---

    @Enumerated(EnumType.STRING)
    @NotNull(message = "chargingStatus cannot be null")
    private ChargingStatus chargingStatus;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "batteryMode cannot be null")
    private BatteryMode batteryMode; // Saved state (AUTO, VPP_DISPATCH, etc.)

    @NotNull
    @Positive(message = "batteryCapacityWh must be greater than 0")
    private double batteryCapacityWh;

    @NotNull
    @PositiveOrZero(message = "batteryRemainingWh cannot be negative")
    private double batteryRemainingWh;

    // Derived value for quick DB queries, though usually calculated in the POJO
    @NotNull
    @Min(value = 0, message = "batterySoc cannot be less than 0")
    @Max(value = 100, message = "batterySoc cannot be greater than 100")
    private int batterySoc;

    // --- Audit Metadata ---

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @NotNull(message = "last updated timestamp cannot be null")
    private Timestamp lastUpdatedAt;
}