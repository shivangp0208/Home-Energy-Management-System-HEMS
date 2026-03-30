package com.hems.project.email_service.exception;

public class FcmNotificationException extends RuntimeException {
    public FcmNotificationException(String message) {
        super(message);
    }

    public FcmNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
