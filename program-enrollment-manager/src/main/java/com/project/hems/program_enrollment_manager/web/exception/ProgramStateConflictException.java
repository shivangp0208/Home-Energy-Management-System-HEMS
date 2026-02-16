package com.project.hems.program_enrollment_manager.web.exception;

public class ProgramStateConflictException extends RuntimeException {
    public ProgramStateConflictException(String msg) {
        super(msg);
    }
}
