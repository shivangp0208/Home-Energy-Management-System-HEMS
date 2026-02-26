package com.hems.project.ADMIN_SERVICE.entity;

import com.hems.project.ADMIN_SERVICE.dto.DispatchEventDto;
import com.hems.project.ADMIN_SERVICE.dto.DispatchMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class DispatchEvent extends QuartzJobBean {


    @Value("${property.config.schedule-topic}")
    private String topic;

    private final KafkaTemplate<String,Object> kafkaTemplate;


    @Override
    protected void executeInternal(JobExecutionContext context) {

        JobDataMap dataMap = context.getMergedJobDataMap();

        UUID eventId = UUID.fromString(dataMap.getString("eventId"));

        DispatchMode mode = DispatchMode.valueOf(
                dataMap.getString("eventMode"));

        Long targetPower = dataMap.getLong("targetPowerW");

        Integer targetSoc = dataMap.getInt("targetSoc");

        List<String> siteIdStrings =
                (List<String>) dataMap.get("siteIds");

        List<UUID> siteIds = siteIdStrings.stream()
                .map(UUID::fromString)
                .toList();

        DispatchEventDto dto=DispatchEventDto.builder()
                .eventId(eventId)
                .eventMode(mode)
                .targetPowerW(targetPower)
                .targetSoc(targetSoc)
                .validSiteIds(siteIds)
                .build();

        sendDispatchCommandToKafka(dto);
        
    }

    private void sendDispatchCommandToKafka(DispatchEventDto dto) {
        //send dispatch command to kafka topic:- schedule-dispatch-event
        try {
            kafkaTemplate.send(topic, dto);
            log.info("dispatch event invoke and send to kafka topic");
        }catch (Exception ex){
            log.error("error in dispatch event invoke and send to kafka topic");
            //and kafka fall back method add karvi if kafka is down then we send manuallyy
        }
    }
}
