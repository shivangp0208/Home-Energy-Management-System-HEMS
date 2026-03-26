package com.project.hems.dispatch_manager_service.exception;

import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MessageConversionException.class)
    public void handleMessageConversionException(MessageConversionException ex) {
        log.error("handleMessageConversionException: " + ex.getMessage());
    }
}
