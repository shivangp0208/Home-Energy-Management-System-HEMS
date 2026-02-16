package com.project.hems.hems_api_contracts.contract.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSuccessfullResponseDto {
    private String message;
    private String to;
    private String from;
    private String subject;
}
