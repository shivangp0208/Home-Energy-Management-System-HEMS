package com.hems.project.email_service.exception;

public class MailSendException extends RuntimeException{
    public MailSendException(String message) {
        super(message);
    }

    public MailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
