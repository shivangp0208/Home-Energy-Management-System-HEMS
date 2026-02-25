package com.project.hems.envoy_manager_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomizedErrorResponse {
    private int statusCode;
    private String error;
    private String message;
}
