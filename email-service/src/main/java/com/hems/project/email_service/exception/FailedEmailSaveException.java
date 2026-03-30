package com.hems.project.email_service.exception;

public class FailedEmailSaveException extends RuntimeException {
    public FailedEmailSaveException(String message) {
        super(message);
    }

    public FailedEmailSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
