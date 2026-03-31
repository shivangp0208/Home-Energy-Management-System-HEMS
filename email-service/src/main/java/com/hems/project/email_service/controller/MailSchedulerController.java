package com.hems.project.email_service.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(
            summary = "schedule email",
            description = "schedule an email to be sent at a specific date and time using quartz scheduler"
    )
    @ApiResponse(responseCode = "200", description = "email scheduled successfully")
    @ApiResponse(responseCode = "400", description = "invalid date time")
    @ApiResponse(responseCode = "500", description = "internal server error")
    @PreAuthorize("hasAuthority('mail:schedule')")
    @PostMapping(value = "/schedule/email",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest){

        log.info("received request to schedule email at {} with timezone {}",
                emailRequest.getDateTime(), emailRequest.getTimeZone());
        try{
            ZonedDateTime dateTime=ZonedDateTime.of(emailRequest.getDateTime(),emailRequest.getTimeZone());
            if (dateTime.isBefore(ZonedDateTime.now())){
                log.warn("invalid schedule time: provided time {} is before current time", dateTime);
                EmailResponse emailResponse=new EmailResponse("date time mist be after current time",false);
                return new ResponseEntity<>(emailResponse,HttpStatus.INTERNAL_SERVER_ERROR);
            }

            JobDetail jobDetail=buildJobDetail(emailRequest);
            Trigger trigger=buildTrigger(jobDetail,dateTime);
            //note jyare apde aa scheduler ni help this schedule kariee so at that time behind the scene
            //quartz atumatic apda mate badhu kare database ma ena jetla pan table hase badhu update kari dese//

            scheduler.scheduleJob(jobDetail,trigger);
            log.info("email scheduled successfully with job name {} and group {}",
                    jobDetail.getKey().getName(),
                    jobDetail.getKey().getGroup());
            EmailResponse emailResponse=new EmailResponse("email scheduled successfully",jobDetail.getKey().getName(),jobDetail.getKey().getGroup(),true);
            return new ResponseEntity<>(emailResponse,HttpStatus.OK);


        }catch (Exception ex) {

            log.error("error while scheduling email: {}", ex.getMessage(), ex);
            EmailResponse response = new EmailResponse(
                    "error while scheduling email, please try again later",
                    false
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
