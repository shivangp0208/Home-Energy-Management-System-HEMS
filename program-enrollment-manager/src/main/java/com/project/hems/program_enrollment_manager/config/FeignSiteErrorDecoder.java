package com.project.hems.program_enrollment_manager.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;

import com.project.hems.program_enrollment_manager.exception.site.SiteArgumentException;
import com.project.hems.program_enrollment_manager.exception.site.SiteResourceNotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignSiteErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String responseBody = extractResponseBody(response);

        // Log error details
        log.error("Feign client error. Method: {}, Status: {}, Body: {}",
                methodKey, status, responseBody);

        // Map status codes to exceptions
        switch (status) {
            case BAD_REQUEST:
                return new SiteArgumentException("Invalid request: " + responseBody);
            case NOT_FOUND:
                return new SiteResourceNotFoundException(responseBody);
            default:
                return new Exception("Unexpected error: " + responseBody);
        }
    }

    private String extractResponseBody(Response response) {
        if (response.body() == null) {
            return "No response body";
        }

        try {
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.error("Failed to read response body", ex);
            return "Error reading response body";
        }
    }

}
