package com.hems.project.virtual_power_plant.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VppVerificationStatusDto {
    private VppVerificationStatus verificationStatus;
    private LocalDateTime submittedForVerificationAt;
}
