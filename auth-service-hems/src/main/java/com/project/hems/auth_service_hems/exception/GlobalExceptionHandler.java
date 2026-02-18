package com.project.hems.auth_service_hems.exception;

import com.project.hems.auth_service_hems.dto.JwtExceptionDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = JwtValidationException.class)
    public ResponseEntity<JwtExceptionDto> handleJwtException(JwtValidationException exception){
        JwtExceptionDto exceptionDto=new JwtExceptionDto("jwt token is invalid", HttpStatus.UNAUTHORIZED,401);
        log.error("JWT validation failed: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRoleException(RoleNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("error", "ROLE_NOT_FOUND");
        response.put("message", ex.getMessage());
        response.put("roleName", ex.getRoleName());
        response.put("status", HttpStatus.NOT_FOUND.value());

        log.error("Role not found: {}", ex.getRoleName());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleUserNotFoundException(UserNotFoundException ex){

        Map<String, Object> response = new HashMap<>();

        response.put("error","USER_NOT_FOUND");
        response.put("message",ex.getMessage());
        response.put("status",HttpStatus.NOT_FOUND.value());

        log.error("User not found: {}", ex.getUserName());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(UserHasAlreadyRoleException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyRoleException(UserHasAlreadyRoleException ex) {
        Map<String, String> errors = new HashMap<>();

        errors.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(UserHasNotRoleException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyRoleException(UserHasNotRoleException ex) {
        Map<String, String> errors = new HashMap<>();

        errors.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);


    }
}
