package com.hems.project.virtual_power_plant.exception;

public class GroupAlreadyPresentException extends RuntimeException {
    public GroupAlreadyPresentException(String msg) {
        super(msg);
    }
}
