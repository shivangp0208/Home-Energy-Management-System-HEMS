package com.project.hems.hems_api_contracts.contract.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSuccessfullRequestDto {

    private String body;
    private String subject;
    private String to;
    private String filePath;

}