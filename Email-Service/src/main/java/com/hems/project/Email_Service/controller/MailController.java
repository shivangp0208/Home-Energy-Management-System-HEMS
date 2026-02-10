package com.hems.project.Email_Service.controller;

import com.hems.project.Email_Service.dto.MailSuccessfullRequestDto;
import com.hems.project.Email_Service.dto.MailSuccessfullResponseDto;
import com.hems.project.Email_Service.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
