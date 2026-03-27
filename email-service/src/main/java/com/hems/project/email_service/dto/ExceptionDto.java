package com.hems.project.email_service.dto;

import org.springframework.http.HttpStatus;

public record ExceptionDto(
        String message,
        HttpStatus status,
        int statusCode
) {}
