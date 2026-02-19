package com.hems.project.Virtual_Power_Plant.exception;

import feign.FeignException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException ex) {

        return ResponseEntity
                .status(ex.status())
                .body(Map.of(
                        "message", "downstream service error",
                        "downstreamStatus", ex.status(),
                        "details", ex.contentUTF8()));
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(GroupAlreadyPresentException.class)
    public Map<String, Object> handleGroupAlreadyPresentException(GroupAlreadyPresentException ex) {

        return Map.of(
                "message", "GROUP_ALREADY_PRESENT",
                "error", ex.getMessage(),
                "code", HttpStatus.CONFLICT.value());
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(SiteGroupNotFoundException.class)
    public Map<String, Object> handleSiteGroupNotFoundException(SiteGroupNotFoundException ex) {

        return Map.of(
                "message", "GROUP_NOT_FOUND",
                "error", ex.getMessage(),
                "code", HttpStatus.CONFLICT.value());
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(SiteGroupStateConflictException.class)
    public Map<String, Object> handleSiteGroupStateConflictException(SiteGroupStateConflictException ex) {

        return Map.of(
                "message", "GROUP_STATE_CONFLICT",
                "error", ex.getMessage(),
                "code", HttpStatus.CONFLICT.value());
    }
}
