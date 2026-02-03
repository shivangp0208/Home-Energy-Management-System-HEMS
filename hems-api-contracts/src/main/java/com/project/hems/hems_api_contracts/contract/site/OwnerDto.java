package com.project.hems.hems_api_contracts.contract.site;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OwnerDto {
    private UUID id;
    private String ownerName;
    private String email;
    private String phoneNo;
    private List<UUID> sitesIds;

}
