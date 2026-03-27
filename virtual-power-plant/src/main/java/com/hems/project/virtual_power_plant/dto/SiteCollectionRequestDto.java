package com.hems.project.virtual_power_plant.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SiteCollectionRequestDto {
    private String collectionName;
    private List<UUID> siteIds;
}
