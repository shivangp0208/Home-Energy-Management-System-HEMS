package com.hems.project.Virtual_Power_Plant.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResourceNotFoundException extends RuntimeException {
    private String message;
    private HttpStatus status;
    private HttpStatus statusCode;

    public ResourceNotFoundException(String message, HttpStatus status, HttpStatus statusCode) {
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
