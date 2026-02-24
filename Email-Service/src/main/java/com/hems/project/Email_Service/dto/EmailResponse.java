package com.hems.project.Email_Service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EmailResponse {

    private boolean success;

    private String jobId;

    private String jobGroup;

    private String message;

    public EmailResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public EmailResponse(String message, String jobGroup, String jobId, boolean success) {
        this.message = message;
        this.jobGroup = jobGroup;
        this.jobId = jobId;
        this.success = success;
    }
}
