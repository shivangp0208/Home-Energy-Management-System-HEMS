package com.hems.project.admin_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hems.project.admin_service.dto.CasePriority;
import com.hems.project.admin_service.dto.CaseSource;
import com.hems.project.admin_service.dto.CaseStatus;
import com.hems.project.admin_service.dto.CaseType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseCreatedResponseDto{
    UUID id;
    String caseNumber;

    CaseType type;
    CasePriority priority;
    CaseStatus status;

    CaseSource source;
    String sourceService;

    UUID siteId;
    UUID vppId;
    UUID dispatchId;

    String title;
    String description;

    String assignedTo;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}