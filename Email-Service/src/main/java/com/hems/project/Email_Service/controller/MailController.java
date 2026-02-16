package com.hems.project.Email_Service.controller;

import com.hems.project.Email_Service.service.MailService;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "mail controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/send-mail")
    public ResponseEntity<MailSuccessfullResponseDto> sendMail(@RequestBody MailSuccessfullRequestDto dto){
        MailSuccessfullResponseDto mailSuccessfullResponseDto = mailService.sendMail(dto);
        return new ResponseEntity<>(mailSuccessfullResponseDto, HttpStatus.OK);
    }

    @PostMapping("/send-mail-html")
    public ResponseEntity<MailSuccessfullResponseDto> sendMailWithHtml(@RequestBody MailSuccessfullRequestDto dto) throws MessagingException, IOException {
        MailSuccessfullResponseDto mailSuccessfullResponseDto = mailService.sendMailWithHtml(dto);
        return new ResponseEntity<>(mailSuccessfullResponseDto, HttpStatus.OK);

    }
}
