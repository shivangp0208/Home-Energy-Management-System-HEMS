package com.hems.project.email_service.exception;

public class DlqProcessingException extends RuntimeException {
    public DlqProcessingException(String message) {
        super(message);
    }

    public DlqProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
