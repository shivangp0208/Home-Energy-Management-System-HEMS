package com.project.hems.auth_service_hems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminRoleRequest {
    @Email(message = "invalid email format")
    @NotNull(message = "email is required")
    private String email;
    @NotNull(message = "roleName is required")
    private String roleName;
}
