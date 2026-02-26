package com.project.hems.envoy_manager_service.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;

import com.project.hems.envoy_manager_service.exception.DuplicateCommandException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulatorErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("decode: decoding message using ErrorDecoder for exception handling");

        HttpStatus status = HttpStatus.valueOf(response.status());
        log.debug("decode: Http status received = " + status);

        String responseBody = extractResponseBody(response);
        log.debug("decode: responseBody received = " + responseBody);

        String[] jsonKeyValue = responseBody.split("\\:");
        String errorMessage = jsonKeyValue[jsonKeyValue.length - 1].replaceAll("\"", "");
        log.debug("decode: error message = " + errorMessage);

        switch (status) {
            case CONFLICT:
                log.error("decode: " + responseBody);
                return new DuplicateCommandException(responseBody);
            default:
                return new RuntimeException("Unexpected error: " + errorMessage);
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
