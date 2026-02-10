package com.project.hems.hems_api_contracts.contract.vpp;

import java.util.UUID;
import lombok.Data;

@Data
public class SiteEnrollRequest {
    private UUID siteId;
    private UUID programId;
    
}
