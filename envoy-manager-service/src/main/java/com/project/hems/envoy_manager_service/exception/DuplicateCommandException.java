package com.project.hems.envoy_manager_service.exception;

public class DuplicateCommandException extends RuntimeException {
    public DuplicateCommandException(String msg) {
        super(msg);
    }
}
