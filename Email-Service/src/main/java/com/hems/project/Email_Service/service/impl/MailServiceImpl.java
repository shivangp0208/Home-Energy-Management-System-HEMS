package com.hems.project.Email_Service.service.impl;

import com.hems.project.Email_Service.exception.MailSendException;
import com.hems.project.Email_Service.service.MailService;
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


//TODO:- ama ek j user ne j apde send karvanu che mail ke tame aa upload karyu che em
//TODO:- vpp manager ne nathi send karanu e toh ene live dashboard ma dekhase
// and mail send thayy and je document hase e apde Supabase Bucket ma store karyu j che

@Slf4j
@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    public MailSuccessfullResponseDto sendMail(EmailEventDto requestDto){
        log.info("controller reach in sendMail service");
        //System.out.println(10/0);
        System.out.println("EMAIL DTO = " + requestDto);

        try {
            SimpleMailMessage mailMessage=new SimpleMailMessage();
            mailMessage.setFrom("hems07293@gmail.com");
            mailMessage.setTo(requestDto.getTo());
            //subject nu format userId/Vppid - and some message e rete rakhvu
            mailMessage.setSubject(requestDto.getSubject());
            mailMessage.setText(requestDto.getBody());
            mailSender.send(mailMessage);
        } catch (MailException e) {
            log.error("failed to send email to {}", requestDto.getTo(), e);
            throw new MailSendException("failed to send email to " + requestDto.getTo(), e);
        }
        MailSuccessfullResponseDto mailSuccessfullResponseDto=MailSuccessfullResponseDto
                .builder()
                .to(requestDto.getTo())
                .from("hems07293@gmail.com")
                .message(requestDto.getBody())
                .subject(requestDto.getSubject())
                .build();
        return mailSuccessfullResponseDto;
    }

    //TODO:-
    //badhu pati jay pachi aa karvanu ke user upload kare e document ne apde send kariee template ma
    //now we send attachment what vpp put for verification
    @Override
    public MailSuccessfullResponseDto sendMailWithAttachment(MailSuccessfullRequestDto requestDto) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("hems07293@gmail.com");
            helper.setTo(requestDto.getTo());
            helper.setSubject(requestDto.getSubject());
            helper.setText(requestDto.getBody(), false);

            //attachment
            if (requestDto.getFilePath() != null) {
                FileSystemResource file =
                        new FileSystemResource(requestDto.getFilePath());

                helper.addAttachment(file.getFilename(), file);
            }

            mailSender.send(mimeMessage);

            return MailSuccessfullResponseDto.builder()
                    .from("hems07293@gmail.com")
                    .to(requestDto.getTo())
                    .subject(requestDto.getSubject())
                    .message("attachment email sent successfully")
                    .build();

        } catch (jakarta.mail.MessagingException | MailException e) {
            log.error("attachment email sending failed for {}", requestDto.getTo(), e);
            throw new MailSendException(
                    "failed to send attachment email to " + requestDto.getTo(),
                    e
            );
        }
    }

    //TODO:-
    //ane completableFurute ma muki devu ke mail toh game tyare send thase apde tara bijo task chalu kari
    //devano ane backend ma muki rakhvanu
    @Override
    public MailSuccessfullResponseDto sendMailWithHtml(MailSuccessfullRequestDto requestDto) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("hems07293@gmail.com");
            helper.setTo(requestDto.getTo());
            helper.setSubject("random mail for testing");

            try (var inputStream = Objects.requireNonNull(
                    MailServiceImpl.class.getResourceAsStream("/templates/email-content.html")
            )) {
                helper.setText(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8), true);
            }

            FileSystemResource logo = new FileSystemResource("/Users/jillspatel/Desktop/hems-logo.png");
            helper.addInline("logo", logo);

            mailSender.send(mimeMessage);

            return MailSuccessfullResponseDto.builder()
                    .message("successfully email sent")
                    .subject(requestDto.getSubject())
                    .from("hems07293@gmail.com")
                    .to(requestDto.getTo())
                    .build();

        } catch (jakarta.mail.MessagingException | IOException | MailException e) {
            log.error("html email sending failed for {}", requestDto.getTo(), e);
            throw new MailSendException("failed to send html email", e);
        }
    }


}
