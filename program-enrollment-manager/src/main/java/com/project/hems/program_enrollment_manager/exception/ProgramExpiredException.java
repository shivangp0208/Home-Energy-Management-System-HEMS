package com.project.hems.program_enrollment_manager.exception;

public class ProgramExpiredException extends RuntimeException {
    public ProgramExpiredException(String msg) {
        super(msg);
    }
}
