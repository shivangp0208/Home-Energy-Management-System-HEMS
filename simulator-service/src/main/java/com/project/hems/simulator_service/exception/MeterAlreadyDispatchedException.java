package com.project.hems.simulator_service.exception;

public class MeterAlreadyDispatchedException extends RuntimeException {
    public MeterAlreadyDispatchedException(String msg) {
        super(msg);
    }
}
