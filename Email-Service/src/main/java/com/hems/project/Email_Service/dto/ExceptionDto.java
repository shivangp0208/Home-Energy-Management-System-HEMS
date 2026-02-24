package com.hems.project.Email_Service.dto;

import org.springframework.http.HttpStatus;

public record ExceptionDto(
        String message,
        HttpStatus status,
        int statusCode
) {}
