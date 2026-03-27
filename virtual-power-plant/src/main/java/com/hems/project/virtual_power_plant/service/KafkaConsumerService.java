package com.hems.project.virtual_power_plant.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "property.config.kafka")
public class KafkaConsumerService {

    @KafkaListener(topics = "${property.config.kafka.vpp-service-dlt-topic}", groupId = "${property.config.kafka.vpp-service-dlt-group-id}")
    public void consumevppRequirement(SignalForImport signalForImport) {
        // TODO: after failing of an event for any site we can send the error detail
        // from here to admin dashboard
        log.error("consumevppRequirement: error for getting the signal " + signalForImport);
    }
}
