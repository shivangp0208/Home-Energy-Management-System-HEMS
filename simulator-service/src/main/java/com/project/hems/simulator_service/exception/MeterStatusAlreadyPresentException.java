package com.project.hems.simulator_service.exception;

public class MeterStatusAlreadyPresentException extends RuntimeException {

    public MeterStatusAlreadyPresentException(String msg) {
        super(msg);
    }
}
