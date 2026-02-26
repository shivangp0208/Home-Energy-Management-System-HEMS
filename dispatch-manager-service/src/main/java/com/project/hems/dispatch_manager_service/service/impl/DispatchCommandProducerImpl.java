package com.project.hems.dispatch_manager_service.service.impl;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.project.hems.dispatch_manager_service.service.DispatchCommandProducer;
import com.project.hems.hems_api_contracts.contract.dispatch.DeviceCommand;
import com.project.hems.hems_api_contracts.contract.vpp.DispatchEventDto;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Setter
public class DispatchCommandProducerImpl implements DispatchCommandProducer  {

    @NotNull(message = "dispatch energy topic is null")
    private String dispatchCommandTopic;

    private final KafkaTemplate<String, DeviceCommand> kafkaTemplate;

    public void processBulkDispatchEvent(DispatchEventDto bulkEvent) {
        
        log.debug("processBulkDispatchEvent: processing Bulk Dispatch Event: {}", bulkEvent.getEventId());
        log.debug("processBulkDispatchEvent: Total sites to dispatch: {}", bulkEvent.getValidSiteIds().size());

        for (UUID siteId : bulkEvent.getValidSiteIds()) {
            
            // 1. Create the individual command
            DeviceCommand deviceCmd = DeviceCommand.builder()
                    .eventId(bulkEvent.getEventId())
                    .siteId(siteId)
                    .programId(bulkEvent.getProgramId())
                    .durationMinutes(bulkEvent.getDurationMinutes())
                    .mode(bulkEvent.getEventMode())
                    .targetPowerW(bulkEvent.getTargetPowerW())
                    .targetSoc(bulkEvent.getTargetSoc())
                    .build();

            // 2. Send it to the Envoy topic
            // We use siteId.toString() as the Kafka Key so commands for the 
            // same site go to the same partition (preserves order)
            kafkaTemplate.send(dispatchCommandTopic, deviceCmd);
            
            log.debug("Sent command to Site: {}", siteId);
        }
        
        log.debug("processBulkDispatchEvent: finished fanning out Event: {}", bulkEvent.getEventId());
    }
}
