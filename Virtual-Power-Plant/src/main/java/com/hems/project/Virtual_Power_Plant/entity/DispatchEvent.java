package com.hems.project.Virtual_Power_Plant.entity;

import com.hems.project.Virtual_Power_Plant.dto.DispatchContext;
import com.project.hems.hems_api_contracts.contract.vpp.DispatchEventDto;
import com.project.hems.hems_api_contracts.contract.vpp.DispatchMode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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

    @Value("${kafka.topic.schedule-topic}")
    private String topicName;

    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Override
    protected void executeInternal(JobExecutionContext context) {

        JobDataMap dataMap = context.getMergedJobDataMap();

        UUID eventId = UUID.fromString(dataMap.getString("eventId"));
        UUID programId = UUID.fromString(dataMap.getString("programId"));

        DispatchContext mode = DispatchMode.valueOf(
                dataMap.getString("eventMode"));

        Long targetPower = dataMap.getLong("targetPowerW");

        Integer targetSoc = dataMap.getInt("targetSoc");

        Integer durationMinutes = dataMap.getInt("durationMinutes");

        List<String> siteIdStrings =
                (List<String>) dataMap.get("siteIds");

        List<UUID> siteIds = siteIdStrings.stream()
                .map(UUID::fromString)
                .toList();

        DispatchEventDto dto=DispatchEventDto.builder()
                .eventId(eventId)
                .programId(programId)
                .eventMode(mode)
                .targetPowerW(targetPower)
                .durationMinutes(durationMinutes)
                .targetSoc(targetSoc)
                .validSiteIds(siteIds)
                .build();

        sendDispatchCommandToKafka(dto);
        
    }

    private void sendDispatchCommandToKafka(DispatchEventDto dto) {
        //send dispatch command to kafka topic:- schedule-dispatch-event
        try {
            kafkaTemplate.send(topicName, dto);
            log.info("dispatch event invoke and send to kafka topic");
        }catch (Exception ex){
            log.error("error in dispatch event invoke and send to kafka topic");
            //and kafka fall back method add karvi if kafka is down then we send manuallyy
        }
    }
}
