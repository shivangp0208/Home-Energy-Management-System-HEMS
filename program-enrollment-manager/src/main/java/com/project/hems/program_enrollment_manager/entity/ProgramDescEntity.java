package com.project.hems.program_enrollment_manager.entity;

import java.util.List;

import org.hibernate.type.SqlTypes;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.envoy.BatteryControl;
import com.project.hems.hems_api_contracts.contract.envoy.GridControl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;

@Table(name = "program_desc")
@Entity
@Data
public class ProgramDescEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long descriptionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false, unique = true)
    private ProgramEntity program;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "load_energy_order", columnDefinition = "jsonb", nullable = false)
    private List<EnergyPriority> loadEnergyOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "surplus_energy_order", columnDefinition = "jsonb", nullable = false)
    private List<EnergyPriority> surplusEnergyOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "grid_control", columnDefinition = "jsonb")
    private GridControl gridControl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "battery_control", columnDefinition = "jsonb")
    private BatteryControl batteryControl;
}
