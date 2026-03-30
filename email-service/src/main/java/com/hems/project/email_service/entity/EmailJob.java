package com.hems.project.email_service.entity;

import com.netflix.discovery.converters.Auto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class EmailJob extends QuartzJobBean {

    private final JavaMailSender mailSender;

    private final MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //so aa class km override karyo apde?
        /*
         * Why do we override this method?
         *
         * This method contains the actual logic that should run
         * when the scheduled Quartz job is triggered.
         *
         * When we schedule the job using scheduler.scheduleJob(),
         * Quartz stores the JobDetail and Trigger (in memory or DB).
         *
         * When the trigger fires at the scheduled time,
         * Quartz automatically invokes this method.
         *
         * The JobDataMap contains the data we passed during scheduling,
         * and here we retrieve that data to execute the task (send email).
         */

        JobDataMap jobDataMap=context.getMergedJobDataMap();


        String subject=jobDataMap.getString("subject");
        String body=jobDataMap.getString("body");
        String recipientEmail=jobDataMap.getString("email");

        sendMail(mailProperties.getUsername(),recipientEmail,subject,body);

    }

    private void sendMail(String fromEmail,String toEmail,String subject,String body){

        try{
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            helper.setSubject(subject);
            helper.setText(body,true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }




    }
}
