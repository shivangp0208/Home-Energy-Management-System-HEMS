package com.hems.project.virtual_power_plant.dto;

import com.hems.project.virtual_power_plant.entity.VppDocumentStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class DocumentVerificationDto {

    private UUID documentId;
    private VppDocumentStatus status;
    private String note;
}
