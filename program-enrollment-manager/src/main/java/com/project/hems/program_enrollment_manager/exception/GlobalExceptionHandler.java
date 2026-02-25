package com.project.hems.program_enrollment_manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.hems.program_enrollment_manager.exception.site.SiteArgumentException;
import com.project.hems.program_enrollment_manager.exception.site.SiteResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProgramNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProgramNotFoundException(ProgramNotFoundException ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error("PROGRAM_NOT_FOUND")
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ErrorResponse.builder()
                .message(ex.getBindingResult()
                        .getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .findFirst().get())
                .error("INVALID_ARGUMENT")
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build();
    }

    @ExceptionHandler(ProgramStateConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleProgramStateConflictException(ProgramStateConflictException ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error("DUPLICATE_REQUEST")
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    @ExceptionHandler(ProgramExpiredException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleProgramExpiredException(ProgramExpiredException ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error("PROGRAM_EXPIRED")
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                .build();
    }

    @ExceptionHandler(SiteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleSiteNotFoundException(SiteNotFoundException ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error("SITE_NOT_FOUND")
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                .build();
    }

    @ExceptionHandler(SiteResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSiteResourceNotFoundException(SiteResourceNotFoundException ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error("SITE_RESOURCE_NOT_FOUND")
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(SiteArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSiteArgumentException(SiteArgumentException ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error("SITE_INVALID_ARGUMENT")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(SiteAlreadyEnroledException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleSiteAlreadyEnroledException(SiteAlreadyEnroledException ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error("SITE_ALREADY_ENROLED")
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

}
