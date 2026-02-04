package com.project.hems.hems_api_contracts.contract.site;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteCreationEvent {

    private UUID siteId;
    private Double batteryCapacityW;
}