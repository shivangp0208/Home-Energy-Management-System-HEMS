package com.hems.project.ADMIN_SERVICE.exception;

public class GroupAlreadyPresentException extends RuntimeException {
    public GroupAlreadyPresentException(String msg) {
        super(msg);
    }
}
