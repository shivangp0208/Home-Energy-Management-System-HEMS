package com.project.hems.envoy_manager_service.model.site;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteCreationEvent {

    private UUID siteId;
    private double batteryCapacityW;
}
