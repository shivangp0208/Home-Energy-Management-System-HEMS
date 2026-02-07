package com.project.hems.program_enrollment_manager.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.hems.program_enrollment_manager.model.SiteStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name ="status")
    private SiteStatus siteStatus;
}
