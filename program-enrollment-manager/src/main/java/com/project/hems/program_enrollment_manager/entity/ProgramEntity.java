package com.project.hems.program_enrollment_manager.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;

import com.project.hems.hems_api_contracts.contract.program.ProgramType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID programId;
    
    @Column(name = "name", nullable = false, updatable = true)
    private String programName;
    
    @Column(name = "start_time", nullable = false, updatable = true)
    private LocalDateTime startDateTime;

    @Column(name = "end_time", nullable = false, updatable = true)
    private LocalDateTime endDateTime;
    
    @Column(name = "type", nullable = false, updatable = true)
    private ProgramType programType;


    @Enumerated(EnumType.STRING)
    @Column(name ="status")
    private ProgramStatus programStatus;
}
