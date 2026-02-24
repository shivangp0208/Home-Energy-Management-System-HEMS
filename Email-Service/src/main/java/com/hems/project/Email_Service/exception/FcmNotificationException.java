package com.hems.project.Email_Service.exception;

public class FcmNotificationException extends RuntimeException {
    public FcmNotificationException(String message) {
        super(message);
    }

    public FcmNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
