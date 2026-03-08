package com.hems.project.ADMIN_SERVICE.dto;

import lombok.Data;

@Data
public class UpdateCaseStatusRequest {
    private CaseStatus newStatus;
    private String actor; // admin email or sub
}