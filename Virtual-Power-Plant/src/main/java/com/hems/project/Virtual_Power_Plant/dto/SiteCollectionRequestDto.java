package com.hems.project.Virtual_Power_Plant.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SiteCollectionRequestDto {
    private String collectionName;
    private List<UUID> siteIds;
}
