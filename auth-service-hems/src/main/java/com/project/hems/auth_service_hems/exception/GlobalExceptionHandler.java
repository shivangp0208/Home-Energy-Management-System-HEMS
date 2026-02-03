package com.project.hems.auth_service_hems.exception;

import com.project.hems.auth_service_hems.dto.JwtExceptionDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = JwtValidationException.class)
    public ResponseEntity<JwtExceptionDto> handleJwtException(JwtValidationException exception){
        JwtExceptionDto exceptionDto=new JwtExceptionDto("jwt token is invalid", HttpStatus.UNAUTHORIZED,401);
        log.error("JWT validation failed: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
    }

}
