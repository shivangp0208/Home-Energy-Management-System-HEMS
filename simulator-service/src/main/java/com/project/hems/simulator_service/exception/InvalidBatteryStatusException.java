package com.project.hems.simulator_service.exception;

public class InvalidBatteryStatusException extends RuntimeException {
    public InvalidBatteryStatusException(String msg) {
        super(msg);
    }
}
