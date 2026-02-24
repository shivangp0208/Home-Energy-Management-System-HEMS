package com.project.hems.SiteManagerService.service;

import com.project.hems.SiteManagerService.external.EmailFeignClientService;
import com.project.hems.SiteManagerService.service.impl.MailService;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import feign.FeignException;
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
        try {
            mailSuccessfullResponseDtoResponseEntity = emailFeignClientService.sendMail(dto);
        } catch (FeignException e) {
            throw new RuntimeException(e);
        }
        return mailSuccessfullResponseDtoResponseEntity;
    }
}
