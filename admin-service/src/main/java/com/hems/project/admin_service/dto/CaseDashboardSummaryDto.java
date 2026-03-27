package com.hems.project.admin_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseDashboardSummaryDto {
    long total;
    long newCount;
    long inProgressCount;
    long resolvedCount;
    long closedCount;

    long criticalCount;
    long highCount;
}