package com.project.hems.hems_api_contracts.contract.site;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@ToString
@Data
@Builder
public class SiteCreationEvent {

    private UUID siteId;
    private Double batteryCapacityW;
}
