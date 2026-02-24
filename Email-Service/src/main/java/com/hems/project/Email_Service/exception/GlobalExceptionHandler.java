package com.hems.project.Email_Service.exception;

import com.hems.project.Email_Service.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FcmNotificationException.class)
    public ResponseEntity<ExceptionDto> handleFcmNotificationException(
            FcmNotificationException ex) {

        ExceptionDto exceptionDto =
                new ExceptionDto(
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        500
                );

        return new ResponseEntity<>(
                exceptionDto,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(FailedEmailSaveException.class)
    public ResponseEntity<ExceptionDto> handleFailedEmailSaveException(
            FailedEmailSaveException ex) {

        ExceptionDto exceptionDto =
                new ExceptionDto(
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        500
                );

        return new ResponseEntity<>(
                exceptionDto,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ExceptionDto> handleEmailSendException(
            MailSendException ex) {

        ExceptionDto exceptionDto =
                new ExceptionDto(
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        500
                );

        return new ResponseEntity<>(
                exceptionDto,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


    @ExceptionHandler(EmailQueueProcessingException.class)
    public ResponseEntity<ExceptionDto> handleEmailQueueProcessingException(
            EmailQueueProcessingException ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionDto dto =
                new ExceptionDto(ex.getMessage(), status, status.value());

        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(DlqProcessingException.class)
    public ResponseEntity<ExceptionDto> handleDlqProcessingException(
            DlqProcessingException ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionDto dto =
                new ExceptionDto(ex.getMessage(), status, status.value());

        return new ResponseEntity<>(dto, status);
    }

    //normally ana sivay koi pan exception avse toh aa handle kari lese
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGlobalException(Exception ex) {

        ExceptionDto exceptionDto =
                new ExceptionDto(
                        "something went wrong",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        500
                );

        return new ResponseEntity<>(
                exceptionDto,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
