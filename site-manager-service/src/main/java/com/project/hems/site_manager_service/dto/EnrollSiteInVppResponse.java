package com.project.hems.site_manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EnrollSiteInVppResponse {
    private UUID siteId;
    private UUID vppId;
    private String vppName;
    private String message;
}
