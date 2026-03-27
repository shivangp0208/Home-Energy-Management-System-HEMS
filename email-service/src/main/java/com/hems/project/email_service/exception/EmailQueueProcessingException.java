package com.hems.project.email_service.exception;

public class EmailQueueProcessingException extends RuntimeException {
    public EmailQueueProcessingException(String message) {
        super(message);
    }

    public EmailQueueProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
