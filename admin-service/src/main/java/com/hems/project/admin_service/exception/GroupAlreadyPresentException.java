package com.hems.project.admin_service.exception;

public class GroupAlreadyPresentException extends RuntimeException {
    public GroupAlreadyPresentException(String msg) {
        super(msg);
    }
}
