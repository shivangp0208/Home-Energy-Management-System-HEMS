package com.hems.project.Virtual_Power_Plant.controller;

import com.hems.project.Virtual_Power_Plant.entity.Region;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaProducer {

    private final KafkaTemplate<String,Region> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Region> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/")
    public String kafkaCheck() {
        log.info("kafkaCheck: dummy message are send to consumer for checking");
        Region region=Region.builder()
                .name("surat")
                .siteId("SITE_001")
                .build();
        kafkaTemplate.send("my-topic", region);
        return "complete";
    }

}
