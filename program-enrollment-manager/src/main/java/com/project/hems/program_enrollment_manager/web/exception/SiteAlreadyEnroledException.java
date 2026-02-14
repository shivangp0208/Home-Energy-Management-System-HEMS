package com.project.hems.program_enrollment_manager.web.exception;

public class SiteAlreadyEnroledException extends RuntimeException {
    public SiteAlreadyEnroledException(String msg) {
        super(msg);
    }
}
