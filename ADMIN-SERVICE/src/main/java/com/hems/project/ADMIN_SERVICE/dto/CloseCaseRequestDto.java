package com.hems.project.ADMIN_SERVICE.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CloseCaseRequestDto {
    @NotBlank
    private String resolutionSummary;

    private String actor;
}