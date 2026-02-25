package com.project.hems.api_gateway_hems.dto;

import org.springframework.http.HttpStatus;

public record ExceptionDto(
        String message,
        HttpStatus status,
        int statusCode
) {}
