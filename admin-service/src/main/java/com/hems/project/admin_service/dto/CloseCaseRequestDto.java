package com.hems.project.admin_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CloseCaseRequestDto {
    @NotBlank
    private String resolutionSummary;

    private String actor;
}