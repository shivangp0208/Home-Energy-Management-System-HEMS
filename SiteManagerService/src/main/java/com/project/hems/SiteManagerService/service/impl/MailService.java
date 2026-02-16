package com.project.hems.SiteManagerService.service.impl;

import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface MailService {
    ResponseEntity<MailSuccessfullResponseDto> sendMail(MailSuccessfullRequestDto dto);

}
