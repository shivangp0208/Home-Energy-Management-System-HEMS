package com.hems.project.ADMIN_SERVICE.exception;

import org.springframework.http.HttpStatus;

public class RegionNotMatchException extends RuntimeException {

    private final String regionName;
    private final HttpStatus status;

    public RegionNotMatchException(String regionName, String message, HttpStatus status) {
        super(message);
        this.regionName = regionName;
        this.status = status;
    }

    public String getRegionName() {
        return regionName;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
