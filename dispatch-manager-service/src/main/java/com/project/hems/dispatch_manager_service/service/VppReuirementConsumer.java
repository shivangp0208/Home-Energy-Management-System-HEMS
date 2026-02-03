package com.project.hems.dispatch_manager_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.hems.project.hems_api_contracts.contract.site.SignalForImport;

@Service
public class VppReuirementConsumer {

@KafkaListener(
    topics = "${property.config.kafka.vpp-service-topic}",groupId = "${property.config.kafka.vpp-service-group-id}"
)   
 public void vppReuirement(SignalForImport signalForImport){
        System.out.println("consumer consumer :- " + signalForImport.getRegionName() + " " + signalForImport.getRequiredPower());
    }
}


