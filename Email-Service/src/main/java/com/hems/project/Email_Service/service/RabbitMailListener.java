package com.hems.project.Email_Service.service;


import com.hems.project.Email_Service.config.AdminNotificationProps;
import com.hems.project.Email_Service.config.MessagingConfig;
import com.hems.project.Email_Service.entity.EmailStatus;
import com.hems.project.Email_Service.entity.FailedEmail;
import com.hems.project.Email_Service.exception.DlqProcessingException;
import com.hems.project.Email_Service.exception.FailedEmailSaveException;
import com.hems.project.Email_Service.exception.FcmNotificationException;
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMailListener {

    private final MailService mailService;
    private final FailedEmailService failedEmailService;
    private final RabbitTemplate rabbitTemplate;
    private final FcmSender fcmSender;
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


/*
 @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeMessageFromQueue(EmailEventDto dto, Channel channel, Message message) throws IOException {
        try {
            log.info("consuming successfully");
            mailService.sendMail(dto);
            log.info("Mail send successfully to {}", dto.getTo());
            //message.getMessageProperties().getDeliveryTag() aa tage darek message mate alag hoy
            //multiple:-false ke apde kaiee chiee ke aa message ne j dhyan ma lo bija badhau
            //so jyare ack thase toh aaj dilivery tag means particular ej message nu thase
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            //aa ack karvanu kam broker kare but ahiya apde prop file ma manual karelu che etle have
            //apde ack karvu padse
        } catch (Exception e) {
            //if email failed thai jay consumer karya pachi toh pachu queue ma jato rese e message ..
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            throw new RuntimeException(e);
        }
    }
 */
//NOW QUESTION:-
//if example 1 divas sudhi mail server gmail down che toh mail send nai thayy and user sign up karse
//but message queue ma avse then send thase queue ma avse
//so ana ledhe khali khotu cpu use thase and compute waste jate..
//SOLUTION??
//1.Exponential Backoff
/*
SO in this apde pehla try karsu nai thayy
wait 1 sec then try karsu nai thayy
wait 2 sec then try karsu nai thayy
wait 4 sec then try karsu nai thayy
wait 8 sec then try karsu nai thayy
wait 16 sec then try karsu nai thayy
so thoda gap ma aa run thase toh vare vare insert then e badhu karvu nai queue ma enqeue and dequeue
 */
//2.DLQ(dead letter queue)
/*
If message retry 5 times and still fail:
Don’t retry forever
Move to DLQ
Log it
Alert admin
Store failed email in DB
simple term apde count rakhelo che count=10 if e mail 10 varr fail thayy che toh e object ne have
pacho queue ma nai store karsu ena have apde DLQ means just normal queue j che ema put kari dese
and admin ne aleert mokli desu so manually devloper joi lese ke su problem che..
 */




//this x-death header queue automatic add kari dey so e extract karu
// and it look like this List<Map<String,Object>>
//[{
//  count: 3,
//  exchange: "email_exchange",
//  queue: "email_queue",
//  reason: "rejected"
//}
//{
//  count: 6,
//  exchange: "email_exchange",
//  queue: "email_queue",
//  reason: "rejected"
//}]...
/*
        // x-death is List<Map<String,Object>>
    then first check karsu ke list che ke nai and empty toh nathi if empty hase toh retry nai thayu
    hoy toh 0 return karai dissu
        if (!(xDeath instanceof List<?> list) || list.isEmpty()) return 0;

    if list ma hase kai toh ene get karsu and so ek map madse ene check karsu e instance map no j che ne
    nai  hoy toh 0 return karai disu
        Object first = list.get(0);
        if (!(first instanceof Map<?,?> map)) return 0;

    now have map ma thi count laisu only and ene retunrn karaisu if int hase toh int and long
    ma hase toh long ma thi int ma karine..

        Object count = map.get("count");
        if (count instanceof Long l) return l.intValue();
        if (count instanceof Integer i) return i;

        otherrwise
         return 0;
 */