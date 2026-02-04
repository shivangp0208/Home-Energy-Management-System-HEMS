package com.project.hems.program_enrollment_manager.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProgramNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProgramNotFoundException(ProgramNotFoundException ex){
        return ErrorResponse.builder()
        .message(ex.getMessage())
        .error("PROGRAM_NOT_FOUND")
        .statusCode(HttpStatus.NOT_FOUND.value())
        .build();
    }
}
