package com.hems.project.Virtual_Power_Plant.dto;

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
