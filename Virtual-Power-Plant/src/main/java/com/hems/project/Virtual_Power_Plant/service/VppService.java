package com.hems.project.Virtual_Power_Plant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.hems.project.hems_api_contracts.contract.site.SignalForImport;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VppService {

    private final KafkaTemplate<String,SignalForImport> kafkaTemplate;
    @Value("${property.config.kafka.vpp-service-topic}")
    public String vppRequirement;

    public String importPower(SignalForImport signalForImport){
        kafkaTemplate.send(vppRequirement, signalForImport);
        System.out.println("vpp requirement is send to dispatch manager and required power is :- "+signalForImport.getRequiredPower());
        return "successfull";

    }

    
}



