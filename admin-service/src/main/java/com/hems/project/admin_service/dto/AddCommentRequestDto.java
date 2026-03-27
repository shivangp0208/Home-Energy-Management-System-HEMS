package com.hems.project.admin_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequestDto {

    @NotBlank
    private String comment;

    @NotBlank
    private String actor; // logged-in admin email/sub
}