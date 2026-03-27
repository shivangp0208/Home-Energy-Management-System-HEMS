package com.project.hems.site_manager_service.service.impl;

import com.project.hems.site_manager_service.external.EmailFeignClientService;
import com.project.hems.site_manager_service.service.MailService;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements MailService {

    private final EmailFeignClientService emailFeignClientService;
    @Override
    public ResponseEntity<MailSuccessfullResponseDto> sendMail(MailSuccessfullRequestDto dto) {

        ResponseEntity<MailSuccessfullResponseDto> mailSuccessfullResponseDtoResponseEntity = null;
            mailSuccessfullResponseDtoResponseEntity = emailFeignClientService.sendMail(dto);
        return mailSuccessfullResponseDtoResponseEntity;
    }
}
