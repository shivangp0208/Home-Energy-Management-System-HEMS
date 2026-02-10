package com.hems.project.Email_Service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MailSuccessfullResponseDto {
    private String message;
    private String to;
    private String from;
    private String subject;
}
