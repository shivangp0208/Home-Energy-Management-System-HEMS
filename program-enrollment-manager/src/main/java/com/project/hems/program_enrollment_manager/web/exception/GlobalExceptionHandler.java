package com.project.hems.program_enrollment_manager.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
