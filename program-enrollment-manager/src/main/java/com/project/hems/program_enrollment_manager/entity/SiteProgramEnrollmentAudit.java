package com.project.hems.program_enrollment_manager.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.hems.program_enrollment_manager.model.SiteStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "site_program_enrollment_audit")
@Data
public class SiteProgramEnrollmentAudit {

    @GeneratedValue(strategy=GenerationType.UUID)
    @Id
    @Column(name = "audit_id")
    private UUID auditId;

    @Column(name = "enrollment_id")
    private UUID enrollmentId;

    @Column(name = "program_id")
    private UUID programId;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private SiteStatus newSiteStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private SiteStatus oldSiteStatus;

    @Column(name = "change_at")
    private LocalDateTime changeAt;

    private String reason;

    @Column(name = "change_by")
    private String changeBy;//here put Vpp id kai vpp e change karyu che or vpp name 
}


// audit_id
// enrollment_id
// program_id
// old_status
// new_status
// changed_at
// reason
// changed_by
