package com.project.hems.program_enrollment_manager.exception;

public class ProgramStateConflictException extends RuntimeException {
    public ProgramStateConflictException(String msg) {
        super(msg);
    }
}
