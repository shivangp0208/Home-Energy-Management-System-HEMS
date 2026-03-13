package com.project.hems.dispatch_manager_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.project.hems.hems_api_contracts.contract.dispatch.DeviceCommand;
import com.project.hems.hems_api_contracts.contract.vpp.DispatchEventDto;
import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    @Value("${property.config.kafka.vpp-service-topic}")
    private String vppServiceTopic;

    @Value("${property.config.kafka.dispatch-event-topic}")
    private String dispatchEventTopic;

    private final DispatchCommandProducer dispatchCommandProducer;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 5000), dltTopicSuffix = ".DLT", autoStartDltHandler = "false")
    @KafkaListener(topics = "${property.config.kafka.dispatch-event-topic}", groupId = "${property.config.kafka.dispatch-event-group-id}")
    public void consumeDispatchEvent(DispatchEventDto bulkEvent) {

        log.info("consumeDispatchEvent: Received Bulk Dispatch Event: {} on kafka topic: {}", bulkEvent.getEventId(),
                dispatchEventTopic);

        dispatchCommandProducer.processBulkDispatchEvent(bulkEvent);
    }

    @KafkaListener(topics = "${property.config.kafka.dispatch-command-dlt-topic}", groupId = "${property.config.kafka.dispatch-command-dlt-group-id}")
    public void consumeDLTDeviceCommand(DeviceCommand failedCommand) {
        // TODO: after failing of an event for any site we can send the error detail
        // from here to admin dashboard
        log.error("consumeDLTDispatchEvent: error implementing dispatch event for event " + failedCommand);
    }

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 5000), dltTopicSuffix = ".DLT", autoStartDltHandler = "false")
    @KafkaListener(topics = "${property.config.kafka.vpp-service-topic}", groupId = "${property.config.kafka.vpp-service-group-id}")
    public void consumevppRequirement(SignalForImport signalForImport) {

        log.info("vppReuirement: received signal for import = {} from topic = {}", signalForImport.getRegionName(),
                vppServiceTopic);
        signalForImport.getRequirement().forEach((siteId, requiredPower) -> {
            System.out.println("siteId:- " + siteId + "powerRequirement:- " + requiredPower);
        });

        // now we create object of DispatchEvent and send to
        // DispatchEventProducerService
        signalForImport.getRequirement().forEach((siteId, requiredPower) -> {

            // TODO:-
            // check lagadvo site jode actual ma che etlo power ke nai if na hoy toh e site
            // ma response back
            // api devano required power is greater than..

            // DispatchEvent dispatchEvent = new DispatchEvent(UUID.randomUUID(),siteId,
            // DispatchEventType.EXPORT_POWER,
            // 110L,
            // 900L,
            // "test export"
            // );
            // dispatchEventProducer.sendDispatchCommands(dispatchEvent);
            log.info("dispatch command send successfull for siteId = {} ", siteId);
        });
    }
}
