package com.project.hems.SiteManagerService.exception;

import com.project.hems.SiteManagerService.dto.ExceptionDto;
import jakarta.validation.ConstraintViolationException;
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

  @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleResourceNotFound(ResourceNotFoundException ex){
    log.warn("Resource not found: {}", ex.getMessage(), ex);
      ExceptionDto exceptionDto=new ExceptionDto(ex.getMessage(),HttpStatus.NOT_FOUND,404);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
    }

  // handles DTO binding errors example:- request ma koi missing che argunment to aa run thase
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
    log.warn("Request validation failed. Error count={}",ex.getBindingResult().getErrorCount(), ex);
        Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
    );
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  // Jpa validation ni error hase toh ama catch thase
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex){
    log.warn("Constraint violation occurred: {}", ex.getMessage(), ex);
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach(cv ->
            errors.put(cv.getPropertyPath().toString(), cv.getMessage())
    );

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }
}
