package com.project.hems.program_enrollment_manager.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "site_program_enrollment")
@Data
public class SiteProgramEnrollmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "enrollment_id", nullable = false, updatable = false, unique = true)
    private UUID enrollmentId;

    @Column(name = "site_id", nullable = false)
    private UUID site;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    @ToString.Exclude
    private ProgramEntity program;

    // TODO: instead of mannually working for maintaining timing use refernce for mssc-beer-service project to add timestamp for automatic timestamp saving
    @Column(name = "enrollment_time")
    private LocalDateTime enrollmentTime = LocalDateTime.now();
}
