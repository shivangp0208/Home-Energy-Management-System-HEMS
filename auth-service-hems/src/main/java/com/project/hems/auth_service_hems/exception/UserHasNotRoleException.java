package com.project.hems.auth_service_hems.exception;

public class UserHasNotRoleException extends RuntimeException {
    public UserHasNotRoleException(String message) {
        super(message);
    }
}
