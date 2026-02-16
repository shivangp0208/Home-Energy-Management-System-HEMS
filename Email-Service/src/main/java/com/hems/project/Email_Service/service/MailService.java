package com.hems.project.Email_Service.service;

import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


//TODO:- ama ek j user ne j apde send karvanu che mail ke tame aa upload karyu che em
//TODO:- vpp manager ne nathi send karanu e toh ene live dashboard ma dekhase
// and mail send thayy and je document hase e apde Supabase Bucket ma store karyu j che

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailSuccessfullResponseDto sendMail(MailSuccessfullRequestDto requestDto){
        System.out.println("EMAIL DTO = " + requestDto);
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom("hems07293@gmail.com");
        mailMessage.setTo(requestDto.getTo());
        //subject nu format userId/Vppid - and some message e rete rakhvu
        mailMessage.setSubject("""
                   [HEMS] Site Created Successfully | Site ID: 10245
                """);
        mailMessage.setText("""
                        your document is currently is under progress
                """);
        mailSender.send(mailMessage);
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
    public MailSuccessfullResponseDto sendMailWithAttachment(MailSuccessfullRequestDto requestDto) throws MessagingException {
        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);

        mimeMessageHelper.setFrom("hems07293@gmail.com");
        mimeMessageHelper.setTo(requestDto.getTo());
//        mimeMessageHelper.setSubject("");

        return null;
    }

    //TODO:-
    //ane completableFurute ma muki devu ke mail toh game tyare send thase apde tara bijo task chalu kari
    //devano ane backend ma muki rakhvanu
    public MailSuccessfullResponseDto sendMailWithHtml(MailSuccessfullRequestDto requestDto) throws MessagingException, IOException {
        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);

        mimeMessageHelper.setFrom("hems07293@gmail.com");
        mimeMessageHelper.setTo(requestDto.getTo());
        mimeMessageHelper.setSubject("random mail for testing");

        try(var inputStream= Objects.requireNonNull(MailService.class.getResourceAsStream("/templates/email-content.html"))){
            mimeMessageHelper.setText(
                    new String(inputStream.readAllBytes(), StandardCharsets.UTF_8),
                    true
            );
        }
        //ahiya inLine resource ma user e je upload karya hoy e fetch karine send karvana
        FileSystemResource logo =
                new FileSystemResource("/Users/jillspatel/Desktop/hems-logo.png");
        mimeMessageHelper.addInline("logo", logo);
        mailSender.send(mimeMessage);

        MailSuccessfullResponseDto mailSuccessfullResponseDto=MailSuccessfullResponseDto
                .builder()
                .message("successfully email sent")
                .subject(requestDto.getSubject())
                .from("hems07293@gmail.com")
                .to(requestDto.getTo())
                .build();
        return mailSuccessfullResponseDto;
    }


}
