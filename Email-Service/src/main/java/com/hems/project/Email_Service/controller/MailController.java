package com.hems.project.Email_Service.controller;

import com.hems.project.Email_Service.service.impl.MailServiceImpl;
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@Tag(name = "mail controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mail")
public class MailController {

    private final MailServiceImpl mailService;

    @Operation(
            summary = "send email",
            description = "send a basic email using provided email event data"
    )
    @ApiResponse(responseCode = "200", description = "email sent successfully")
    @PreAuthorize("hasAuthority('mail:send')")
    @PostMapping("/send-mail")
    public ResponseEntity<MailSuccessfullResponseDto> sendMail(@RequestBody EmailEventDto dto){

        log.info("sending email to recipients");

        try {
            MailSuccessfullResponseDto response = mailService.sendMail(dto);
            log.info("email sent successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error sending email: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Operation(
            summary = "send html email",
            description = "send an email with html content"
    )
    @ApiResponse(responseCode = "200", description = "html email sent successfully")
    @PreAuthorize("hasAuthority('mail:send')")
    @PostMapping("/send-mail-html")
    public ResponseEntity<MailSuccessfullResponseDto> sendMailWithHtml(
            @RequestBody MailSuccessfullRequestDto dto) {

        log.info("sending html email");

        try {
            MailSuccessfullResponseDto response = mailService.sendMailWithHtml(dto);
            log.info("html email sent successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error sending html email: {}", e.getMessage(), e);
            throw e;
        }
    }

    //shceduled email..
    //like admin need to send real time update so they send with predefine time..
    //note:- normal sceduler static hoy means apde time na api sakiee e static daily ej time per chale
    //but if we need ke atla vage ne atli minite e user e kedhu so e possile nathi
    //@Scheduled(cron = "") ama
    //here we implement using quartz scheduler..





    //todo:-
    //if some one payment is pending so that also we send like ke aa user ne 5 vage mail nakhi devo ..


}
