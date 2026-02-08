package com.project.hems.hems_api_contracts.contract.program;

import java.time.LocalDateTime;
import java.util.UUID;
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
