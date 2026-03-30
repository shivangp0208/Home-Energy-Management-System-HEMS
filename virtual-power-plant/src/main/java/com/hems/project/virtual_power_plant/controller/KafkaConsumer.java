package com.hems.project.virtual_power_plant.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;

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

        log.info("Kafka topic = {} consume message : {}","${property.config.kafka.raw-energy-topic}",meterSnapshot);
        messagingTemplate.convertAndSend(
            "/topic/meter",
            meterSnapshot
        );
        log.info("send kafka message to web socker broker = {} ","topic/meter");
    }

}











