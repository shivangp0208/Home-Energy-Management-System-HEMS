package com.hems.project.ADMIN_SERVICE.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferCaseRequestDto {

    @NotBlank
    private String newAssignee;  // admin email/sub

    @NotBlank
    private String actor;        // who is doing transfer (admin email/sub)

    private String reason;       // optional note
}