package com.hems.project.email_service.service.impl;

import com.hems.project.email_service.config.AdminNotificationProps;
import com.hems.project.email_service.config.MessagingConfig;
import com.hems.project.email_service.entity.EmailStatus;
import com.hems.project.email_service.entity.FailedEmail;
import com.hems.project.email_service.exception.DlqProcessingException;
import com.hems.project.email_service.exception.FailedEmailSaveException;
import com.hems.project.email_service.exception.FcmNotificationException;
import com.hems.project.email_service.repository.FailedEmailRepo;
import com.hems.project.email_service.service.FailedEmailService;
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FailedEmailServiceImpl implements FailedEmailService {

    private final FailedEmailRepo failedEmailRepo;

    public FailedEmail addFailedInDatabase(FailedEmail failedEmail){

        FailedEmail save;
        try {
            save = failedEmailRepo.save(failedEmail);
        } catch (Exception e) {
            throw new FailedEmailSaveException("failed to save FailedEmail into database", e);
        }
        return save;
    }

    @Component
    @Slf4j
    @RequiredArgsConstructor
    public static class RabbitMailListenerImpl {

        private final MailServiceImpl mailService;
        private final FailedEmailServiceImpl failedEmailService;
        private final RabbitTemplate rabbitTemplate;
        private final FcmSenderImpl fcmSender;
        private final AdminNotificationProps adminNotificationProps;

        @RabbitListener(queues = MessagingConfig.MAIN_QUEUE,ackMode = "MANUAL")
        public void consumeMessageFromQueue(EmailEventDto dto, Channel channel, Message message) throws IOException {
            long tagName=message.getMessageProperties().getDeliveryTag();
            int retryCount=getRetryCountFromXDeath(message);
            try {
                log.info("consuming successfully");
                mailService.sendMail(dto);
                log.info("Mail send successfully to {}", dto.getTo());
                channel.basicAck(tagName,false);
                //remove from the queue
            } catch (Exception e) {
                log.error("mail failed retry count={}", retryCount);
                //ahiya count check karsu
                if(retryCount>=5){
                    //send to DLQ
                    //make new config one queue and in that queue put this
                    rabbitTemplate.convertAndSend(
                            MessagingConfig.DLQ_EXCHANGE,
                            MessagingConfig.DLQ_ROUTING_KEY,
                            dto
                    );
                    log.info("goes into DLQ");
                    channel.basicAck(tagName,false);//actual queue ma have nathi nakhvanu
                    //DLQ ma nakhvanu che etle false karelu che

                }else{
                    //so goes in retry queue
                    log.info("goes into reject queue");
                    channel.basicReject(tagName,false);
                }
            }
        }

        //DLQ ma jato ryo so tya thi consume karsu and database ma save karsu and then push notification
        //send karsu to admin
        @RabbitListener(queues = MessagingConfig.DLQ_QUEUE)
        public void consumeDlq(EmailEventDto dto,Channel channel,Message message){
            Map<String,String> resp=new HashMap<>();
            try{
                System.out.println("consume message in dlq is "+dto);
                FailedEmail failedEmail=FailedEmail.builder()
                        .to(dto.getTo())
                        .body(dto.getBody())
                        .failedAt(LocalDateTime.now())
                        .retryCount(5)
                        .status(EmailStatus.FAILED)
                        .subject(dto.getSubject())
                        .build();
                 FailedEmail failed = failedEmailService.
                         addFailedInDatabase(failedEmail);
                 log.info("successfully save in the database failedEmail id {}",failed.getId());
                 //send web push notification to admin using firebase push notification

                notifyAdmins(dto, failed.getId());

            }catch (FailedEmailSaveException | FcmNotificationException ex) {
                log.error("known error while processing DLQ", ex);
                throw ex;

            } catch (Exception ex) {
                //kai unexpceted error avse toh ene DLQ error kaine throw kariee chie
                log.error("unexpected error while processing DLQ", ex);
                throw new DlqProcessingException(
                        "unexpected failure while processing DLQ message",
                        ex
                );
            }


        }




        private int getRetryCountFromXDeath(Message message) {
            Object xDeath = message.getMessageProperties().getHeaders().get("x-death");

            if (!(xDeath instanceof List<?> list) || list.isEmpty()) return 0;

            // x-death aa format ma hoy  List<Map<String,Object>>
            Object first = list.get(0);
            if (!(first instanceof Map<?,?> map)) return 0;

            Object count = map.get("count");
            if (count instanceof Long l) return l.intValue();
            if (count instanceof Integer i) return i;

            return 0;
        }

        private void notifyAdmins(EmailEventDto dto, Long failedId) {
            String title = "Email failed (DLQ)";
            String body = "To: " + dto.getTo() + " | Subject: " + dto.getSubject() + " | id=" + failedId;

            for (String token : adminNotificationProps.getDeviceTokens()) {
                fcmSender.send(token, title, body);
            }
        }

    }
}
