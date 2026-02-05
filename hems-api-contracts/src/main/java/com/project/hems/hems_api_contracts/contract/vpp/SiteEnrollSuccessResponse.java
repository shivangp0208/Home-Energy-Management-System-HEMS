package com.project.hems.hems_api_contracts.contract.vpp;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiteEnrollSuccessResponse {
    private boolean success;
    private String message;
    private UUID siteId;
    private UUID programId;
    private Instant enrollTime;
    
}
