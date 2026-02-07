package com.project.hems.program_enrollment_manager.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SiteProgramEnrollmentAuditDto {

    private UUID auditId;
    private UUID enrollmentId;

    private UUID programId;
    private SiteStatus newSiteStatus;

    private SiteStatus oldSiteStatus;
    private LocalDateTime changeAt;

    private String reason;
    private String changeBy;
    
}
