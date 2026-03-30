package com.project.hems.site_manager_service.service;

import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import org.springframework.http.ResponseEntity;

public interface MailService {
    ResponseEntity<MailSuccessfullResponseDto> sendMail(MailSuccessfullRequestDto dto);

}
