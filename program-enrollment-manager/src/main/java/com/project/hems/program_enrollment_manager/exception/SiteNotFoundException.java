package com.project.hems.program_enrollment_manager.exception;

public class SiteNotFoundException extends RuntimeException{
    public SiteNotFoundException(String msg) {
        super(msg);
    }
}
