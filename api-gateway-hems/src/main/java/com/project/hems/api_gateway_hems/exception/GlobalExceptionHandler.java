package com.project.hems.api_gateway_hems.exception;

import com.project.hems.api_gateway_hems.dto.ExceptionDto;
import com.project.hems.api_gateway_hems.exception.*;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(YamlLoadKeysException.class)
    public ResponseEntity<ExceptionDto> handleYamlLoadKeysException(YamlLoadKeysException ex) {
        log.error("YAML load failed", ex);
        return build(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Auth0UserNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleAuth0UserNotFound(Auth0UserNotFoundException ex) {
        return build(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Auth0MgmtTokenException.class)
    public ResponseEntity<ExceptionDto> handleAuth0MgmtToken(Auth0MgmtTokenException ex) {
        log.error("auth0 mgmt token error", ex);
        return build(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Auth0ChangePasswordEmailException.class)
    public ResponseEntity<ExceptionDto> handleAuth0ChangePassword(Auth0ChangePasswordEmailException ex) {
        log.error("auth0 change_password failed", ex);
        return build(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Auth0CreateDbUserException.class)
    public ResponseEntity<ExceptionDto> handleAuth0CreateDbUser(Auth0CreateDbUserException ex) {
        log.error("auth0 create DB user failed", ex);
        return build(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Auth0LinkIdentityException.class)
    public ResponseEntity<ExceptionDto> handleAuth0LinkIdentity(Auth0LinkIdentityException ex) {
        log.error("Auth0 link identity failed", ex);
        return build(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Catch base auth0 exception if you keep it
    @ExceptionHandler(Auth0Exception.class)
    public ResponseEntity<ExceptionDto> handleAuth0Exception(Auth0Exception ex) {
        log.error("Auth0 general error", ex);
        return build(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(cv -> errors.put(cv.getPropertyPath().toString(), cv.getMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<com.project.hems.api_gateway_hems.dto.ExceptionDto> handleAny(Exception ex) {
        log.error("unhandled exception", ex);
        return build("internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionDto> build(String message, HttpStatus status) {
        ExceptionDto dto = new ExceptionDto(message, status, status.value());
        return ResponseEntity.status(status).body(dto);
    }
}