package com.hems.project.Email_Service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class EmailRequest {

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String body;

    @NonNull
    private LocalDateTime dateTime;

    @NotNull
    private ZoneId timeZone;

}
