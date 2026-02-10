package com.project.hems.SiteManagerService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignVppRequest {

    @NotNull
    private UUID vppId;

    //optional che ui mate jove toh j 
    private String vppName;

    // optional
    private String approvedBy;
    
    private String note;
}
