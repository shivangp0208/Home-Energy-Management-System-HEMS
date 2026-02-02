package com.hems.project.Virtual_Power_Plant.controller;


import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import com.hems.project.Virtual_Power_Plant.entity.simulator.MeterSnapshot;

@Slf4j
@Component
public class KafkaConsumer {
private final SimpMessagingTemplate messagingTemplate;

    public KafkaConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "${property.config.kafka.raw-energy-topic}",
                   groupId = "${property.config.kafka.raw-energy-group-id}")
    public void consumeRawMeterReadings(MeterSnapshot meterSnapshot) {

        log.info("Kafka received: {}", meterSnapshot);

        messagingTemplate.convertAndSend(
            "/topic/meter",
            meterSnapshot
        );
    }

}











