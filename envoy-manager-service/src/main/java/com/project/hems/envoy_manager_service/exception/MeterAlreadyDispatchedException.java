package com.project.hems.envoy_manager_service.exception;

public class MeterAlreadyDispatchedException extends RuntimeException {
    public MeterAlreadyDispatchedException(String msg) {
        super(msg);
    }
}
