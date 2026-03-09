package com.project.hems.envoy_manager_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public CustomizedErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        return CustomizedErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(DuplicateCommandException.class)
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public CustomizedErrorResponse handleDuplicateCommandException(DuplicateCommandException ex) {
        return CustomizedErrorResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .error("DUPLICATE_DISPATCH_COMMAND")
                .message(ex.getMessage())
                .build();
    }
}
