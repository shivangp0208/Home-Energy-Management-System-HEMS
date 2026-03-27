package com.hems.project.email_service.controller;

import com.hems.project.email_service.dto.EmailRequest;
import com.hems.project.email_service.dto.EmailResponse;
import com.hems.project.email_service.entity.EmailJob;
import com.netflix.discovery.converters.Auto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api")
public class MailSchedulerController {

    private final Scheduler scheduler;

    @GetMapping("/get")
    public ResponseEntity<String> getApiTest(){
        String res="swdesfrdghjh";
        return ResponseEntity.ok().body(res);
    }


    @PostMapping(value = "/schedule/email",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest){
        try{
            ZonedDateTime dateTime=ZonedDateTime.of(emailRequest.getDateTime(),emailRequest.getTimeZone());
            if (dateTime.isBefore(ZonedDateTime.now())){
                EmailResponse emailResponse=new EmailResponse("date time mist be after current time",false);
                return new ResponseEntity<>(emailResponse,HttpStatus.INTERNAL_SERVER_ERROR);
            }

            JobDetail jobDetail=buildJobDetail(emailRequest);
            Trigger trigger=buildTrigger(jobDetail,dateTime);
            //note jyare apde aa scheduler ni help this schedule kariee so at that time behind the scene
            //quartz atumatic apda mate badhu kare database ma ena jetla pan table hase badhu update kari dese//
            scheduler.scheduleJob(jobDetail,trigger);
            EmailResponse emailResponse=new EmailResponse("email scheduled successfully",jobDetail.getKey().getName(),jobDetail.getKey().getGroup(),true);
            return new ResponseEntity<>(emailResponse,HttpStatus.OK);


        }catch (Exception ex){
            log.error("error while sending email",ex);
            EmailResponse emailResponse=new EmailResponse("error while scheduleEmail please try again later",false);
            return new ResponseEntity<>(emailResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //one job have many trigger
    //but one trigger only have one job

    //first we make jobdetails and trigger
    //jobdetails
    private JobDetail buildJobDetail(EmailRequest scheduledEmailRequest){
        //job data map hold the actual data je apde send karvano che jyare tigger execute thayy
        JobDataMap jobDataMap=new JobDataMap();
        jobDataMap.put("email",scheduledEmailRequest.getEmail());
        jobDataMap.put("subject",scheduledEmailRequest.getSubject());
        jobDataMap.put("body",scheduledEmailRequest.getBody());


        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(),"email jobs")
                .withDescription("send email job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    //job trigger
    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(),"email-triggers")
                .withDescription("send email trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
