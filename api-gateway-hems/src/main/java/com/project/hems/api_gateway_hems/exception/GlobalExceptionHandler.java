package com.project.hems.api_gateway_hems.exception;

import com.project.hems.api_gateway_hems.dto.ExceptionDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for API Gateway
 * Handles all exceptions and converts them to proper HTTP responses
 * Order = HIGHEST_PRECEDENCE ensures this is processed first
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== Auth0 Exceptions ====================

    @ExceptionHandler(YamlLoadKeysException.class)
    public ResponseEntity<ExceptionDto> handleYamlLoadKeysException(YamlLoadKeysException ex) {
        log.error("[v0] YAML load failed: {}", ex.getMessage());
        return build(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Auth0UserNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleAuth0UserNotFound(Auth0UserNotFoundException ex) {
        log.warn("[v0] Auth0 user not found: {}", ex.getMessage());
        return build(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Auth0MgmtTokenException.class)
    public ResponseEntity<ExceptionDto> handleAuth0MgmtToken(Auth0MgmtTokenException ex) {
        log.error("[v0] Auth0 management token error: {}", ex.getMessage());
        return build(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Auth0ChangePasswordEmailException.class)
    public ResponseEntity<ExceptionDto> handleAuth0ChangePassword(Auth0ChangePasswordEmailException ex) {
        log.error("[v0] Auth0 change password failed: {}", ex.getMessage());
        return build(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Auth0CreateDbUserException.class)
    public ResponseEntity<ExceptionDto> handleAuth0CreateDbUser(Auth0CreateDbUserException ex) {
        log.error("[v0] Auth0 create DB user failed: {}", ex.getMessage());
        return build(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Auth0LinkIdentityException.class)
    public ResponseEntity<ExceptionDto> handleAuth0LinkIdentity(Auth0LinkIdentityException ex) {
        log.error("[v0] Auth0 link identity failed: {}", ex.getMessage());
        return build(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Auth0Exception.class)
    public ResponseEntity<ExceptionDto> handleAuth0Exception(Auth0Exception ex) {
        log.error("[v0] Auth0 general error: {}", ex.getMessage());
        return build(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    // ==================== Validation Exceptions ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        log.warn("[v0] Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex) {
        log.warn("[v0] Constraint violation: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(cv -> errors.put(cv.getPropertyPath().toString(), cv.getMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // ==================== Response Status Exception ====================

    /**
     * Handle ResponseStatusException (thrown by filters)
     * This is the CORRECT way for WebFilter to reject requests
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDto> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("[v0] Response status exception: {} - {}", ex.getStatusCode(), ex.getReason());
        return build(ex.getReason(), (HttpStatus) ex.getStatusCode());
    }

    // ==================== WebClient Exceptions ====================

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ExceptionDto> handleWebClientException(WebClientResponseException ex) {
        log.error("[v0] WebClient error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());

        String message = String.format("External service error: %s", ex.getMessage());
        HttpStatus status = (HttpStatus) ex.getStatusCode();

        return build(message, status);
    }

    // ==================== Generic Exceptions ====================

    /**
     * Catch-all handler for any unhandled exceptions
     * This ensures all errors return proper JSON responses
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGenericException(Exception ex) {
        log.error("[v0] Unhandled exception", ex);

        String message = "Internal server error: " + ex.getClass().getSimpleName();
        return build(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== Helper Methods ====================

    /**
     * Build ExceptionDto response
     */
    private ResponseEntity<ExceptionDto> build(String message, HttpStatus status) {
        ExceptionDto dto = new ExceptionDto(message, status, status.value());
        return ResponseEntity.status(status).body(dto);
    }
}