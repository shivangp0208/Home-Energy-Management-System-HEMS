package com.project.hems.SiteManagerService.external;


import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Email-Service")
public interface EmailFeignClientService {
    @PostMapping("/api/v1/mail/send-mail")
    ResponseEntity<MailSuccessfullResponseDto> sendMail(@RequestBody MailSuccessfullRequestDto dto);

}