package com.hems.project.virtual_power_plant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class SiteCollectionResponseDto {
    private String message;
    private String collectionName;
    private List<UUID> siteIds;
}
