package com.project.hems.api_gateway_hems.dto;


import lombok.Data;

@Data
public class ActivateSessionRequest {
    private String fingerprint;
}