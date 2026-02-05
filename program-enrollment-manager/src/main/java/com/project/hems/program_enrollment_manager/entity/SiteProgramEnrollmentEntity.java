package com.project.hems.program_enrollment_manager.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "site_program_enrollment")
@Data
public class SiteProgramEnrollmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "enrollment_id", nullable = false, updatable = false, unique = true)
    private UUID enrollmentId;

    @Column(name = "site_id", nullable = false)
    private UUID siteId;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private ProgramEntity program;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate = LocalDateTime.now();

    @Column(name = "status")
    private String status = "ACTIVE";
}
