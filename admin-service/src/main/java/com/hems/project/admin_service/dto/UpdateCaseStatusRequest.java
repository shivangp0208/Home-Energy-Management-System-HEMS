package com.hems.project.admin_service.dto;

import lombok.Data;

@Data
public class UpdateCaseStatusRequest {
    private CaseStatus newStatus;
    private String actor; // admin email or sub
}