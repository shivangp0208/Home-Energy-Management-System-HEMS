package com.project.hems.program_enrollment_manager.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;
import com.project.hems.hems_api_contracts.contract.program.ProgramType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "program")
@Data
public class ProgramEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID programId;

    @Column(name = "name", nullable = false, updatable = true)
    private String programName;

    @Column(name = "start_time", nullable = false, updatable = true)
    private LocalDate startDate;

    @Column(name = "end_time", nullable = false, updatable = true)
    private LocalDate endDate;

    @Column(name = "type", nullable = false, updatable = true)
    private ProgramType programType;

    @OneToOne(mappedBy = "program", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProgramDescEntity programDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgramStatus programStatus;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "site_id")
    private List<UUID> sites = new ArrayList<>();

}
