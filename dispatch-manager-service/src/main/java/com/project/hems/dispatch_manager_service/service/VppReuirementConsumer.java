package com.project.hems.dispatch_manager_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.hems.project.hems_api_contracts.contract.site.SignalForImport;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Setter
public class VppReuirementConsumer {

    @Value("${property.config.kafka.vpp-service-topic}")
    private String vppServiceTopic;

    @KafkaListener(topics = "${property.config.kafka.vpp-service-topic}", groupId = "${property.config.kafka.vpp-service-group-id}")
    public void vppReuirement(SignalForImport signalForImport) {
        
        log.info("vppReuirement: received signal for import = {} from topic = {}", signalForImport, vppServiceTopic);
    }
}
