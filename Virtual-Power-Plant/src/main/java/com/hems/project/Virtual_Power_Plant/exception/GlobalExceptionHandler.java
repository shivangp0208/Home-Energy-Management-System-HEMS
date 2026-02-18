package com.hems.project.Virtual_Power_Plant.exception;

import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException ex) {

        return ResponseEntity
                .status(ex.status())
                .body(Map.of(
                        "message", "downstream service error",
                        "downstreamStatus", ex.status(),
                        "details", ex.contentUTF8()
                ));
    }
}

