package com.project.hems.dispatch_manager_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.dispatch.DispatchEvent;
import com.project.hems.hems_api_contracts.contract.dispatch.DispatchEventType;
import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class VppReuirementConsumer {

    @Value("${property.config.kafka.vpp-service-topic}")
    private String vppServiceTopic;

    private final DispatchEventProducer dispatchEventProducer;

    @KafkaListener(topics = "${property.config.kafka.vpp-service-topic}", groupId = "${property.config.kafka.vpp-service-group-id}")
    public void vppReuirement(SignalForImport signalForImport) {
        
        log.info("vppReuirement: received signal for import = {} from topic = {}", signalForImport.getRegionName(), vppServiceTopic);
        signalForImport.getRequirement().forEach((siteId,requiredPower)->{
            System.out.println("siteId:- "+siteId +"powerRequirement:- "+requiredPower);
        });

        //now we create object of DispatchEvent and send  to DispatchEventProducerService
        signalForImport.getRequirement().forEach((siteId,requiredPower)->{
        
            //TODO:-
            //check lagadvo site jode actual ma che etlo power ke nai if na hoy toh e site ma response back
            //api devano required power is greater than..
            DispatchEvent dispatchEvent = new DispatchEvent(
            UUID.randomUUID(),
            siteId,
            DispatchEventType.EXPORT_POWER,
            110L,
            900L,
            List.of(EnergyPriority.BATTERY, EnergyPriority.GRID),
            "test export"
            );
            dispatchEventProducer.sendDispatchCommands(dispatchEvent);
            log.info("dispatch command send successfull for siteId = {} ",siteId);
    });
}

}