package com.hems.project.Email_Service.service;


import com.hems.project.Email_Service.config.MessagingConfig;
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMailListener {

    private final MailService mailService;

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeMessageFromQueue(EmailEventDto dto){
        log.info("consuming successfully");
        mailService.sendMail(dto);
        log.info("Mail send successfully to {}",dto.getTo());
    }

}
