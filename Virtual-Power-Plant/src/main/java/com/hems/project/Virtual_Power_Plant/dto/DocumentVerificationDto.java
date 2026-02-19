package com.hems.project.Virtual_Power_Plant.dto;

import com.hems.project.Virtual_Power_Plant.entity.VppDocumentStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class DocumentVerificationDto {

    private UUID documentId;
    private VppDocumentStatus status;
    private String note;
}
