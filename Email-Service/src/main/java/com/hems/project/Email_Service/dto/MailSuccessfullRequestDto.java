package com.hems.project.Email_Service.dto;

import lombok.Data;

@Data
public class MailSuccessfullRequestDto {
    private String body;
    private String subject;
    private String to;
}
