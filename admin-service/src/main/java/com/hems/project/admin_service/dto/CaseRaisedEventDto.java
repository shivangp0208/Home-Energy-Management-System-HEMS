package com.hems.project.admin_service.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseRaisedEventDto{
    // messaging metadata
    @NotBlank String dedupeKey;        // e.g. "OFFLINE:SITE:<siteId>" or "FAILED_CMD:DISPATCH:<dispatchId>"
    @NotNull LocalDateTime occurredAt; // when detected in source service

    // who raised it
    @NotNull CaseSource source;        // SITE / VPP / SYSTEM / ADMIN
    @NotBlank String sourceService;    // "SITE-SERVICE" / "VPP-SERVICE" / "HEARTBEAT-SCHEDULER"

    // case content
    @NotNull CaseType type;
    @NotNull CasePriority priority;

    UUID siteId;
    UUID vppId;
    UUID dispatchId;

    @NotBlank String title;
    String description;

    // optional: extra data for debugging/reporting
    Map<String, Object> metadata;
}