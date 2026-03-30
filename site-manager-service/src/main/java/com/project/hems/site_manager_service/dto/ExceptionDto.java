package com.project.hems.site_manager_service.dto;

import org.springframework.http.HttpStatus;

public record ExceptionDto(
        String message,
        HttpStatus status,
        int statusCode
) {}
