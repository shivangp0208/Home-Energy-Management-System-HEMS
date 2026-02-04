package com.project.hems.program_enrollment_manager.web.exception;

public class ProgramNotFoundException extends RuntimeException {
    public ProgramNotFoundException(String msg) {
        super(msg);
    }
}
