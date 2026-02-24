package com.hems.project.Email_Service.service;

import com.hems.project.Email_Service.exception.MailSendException;
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public interface MailService {
     MailSuccessfullResponseDto sendMail(EmailEventDto requestDto);

     MailSuccessfullResponseDto sendMailWithAttachment(MailSuccessfullRequestDto requestDto);

     MailSuccessfullResponseDto sendMailWithHtml(MailSuccessfullRequestDto requestDto);

}
