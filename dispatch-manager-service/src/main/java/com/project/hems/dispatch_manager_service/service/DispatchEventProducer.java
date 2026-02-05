package com.project.hems.dispatch_manager_service.service;

import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.dispatch.DispatchEvent;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
@ConfigurationProperties(prefix = "property.config.kafka")
public class DispatchEventProducer {

    private String dispatchEnergyTopic;

    private final KafkaTemplate<String, DispatchEvent> kafkaTemplate;

    public void sendDispatchCommands(DispatchEvent dispatchEvent) {
        log.info("sendDispatchCommands: sending dummy dispatch command to envoy");
        log.debug("sendDispatchCommands: command value = " + dispatchEvent);
        kafkaTemplate.send(dispatchEnergyTopic, dispatchEvent);
        log.debug("sendDispatchCommands: sent command successfully over kafka topic = " + dispatchEnergyTopic);
    }
}
