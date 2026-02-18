package com.project.hems.auth_service_hems.exception;

public class UserHasAlreadyRoleException extends RuntimeException {
    public UserHasAlreadyRoleException(String message) {
        super(message);
    }
}
