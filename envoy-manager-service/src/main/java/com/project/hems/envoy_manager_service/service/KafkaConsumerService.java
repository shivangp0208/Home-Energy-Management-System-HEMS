package com.project.hems.envoy_manager_service.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.exception.DuplicateCommandException;
import com.project.hems.hems_api_contracts.contract.dispatch.DeviceCommand;
import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;
import com.project.hems.hems_api_contracts.contract.site.SiteCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
@Component
@ConfigurationProperties(prefix = "property.config.kafka")
public class KafkaConsumerService {

        private String rawEnergyTopic;
        private String dispatchCommandTopic;
        private String siteCreationTopic;

        private final DispatchCommandService dispatchCommandService;
        private final SimulatorFeignClientService simulatorFeignClientService;

        @KafkaListener(topics = "${property.config.kafka.raw-energy-topic}", groupId = "${property.config.kafka.raw-energy-group-id}")
        public void consumeRawMeterReadings(MeterSnapshot meterSnapshot) {

                log.info(
                                "consumeRawMeterReadings: received raw meter snapshot from topic={} siteId={} meterId={} timestamp={}",
                                rawEnergyTopic,
                                meterSnapshot.getSiteId(),
                                meterSnapshot.getMeterId(),
                                meterSnapshot.getTimestamp());

                log.debug(
                                "consumeRawMeterReadings: payload={}",
                                meterSnapshot);

                // TODO: if want to send refined data directly to UI, implement this
                // webSocket.convertAndSend("/topic/meter/" + meterSnapshot.getMeterId(),
                // meterSnapshot);

                // log.debug("consumeRawMeterReadings: forwarding raw snapshot to aggregation
                // service");
                // meterAggregationService.process(meterSnapshot);
        }

        @KafkaListener(topics = "${property.config.kafka.dispatch-command-topic}", groupId = "${property.config.kafka.dispatch-command-group-id}")
        public void consumeDispatchCommand(DeviceCommand deviceCommand) {

                log.info(
                                "consumedeviceCommand: received dispatch command from topic={} eventId={} siteId={}  programId={} eventType={}",
                                dispatchCommandTopic,
                                deviceCommand.getEventId(),
                                deviceCommand.getSiteId(),
                                deviceCommand.getProgramId(),
                                deviceCommand.getMode());

                log.debug(
                                "consumedeviceCommand: raw dispatch payload={}",
                                deviceCommand);
                try {
                        log.debug(
                                        "consumedeviceCommand: translated dispatch command for siteId={} command={}",
                                        deviceCommand.getSiteId(),
                                        deviceCommand);

                        dispatchCommandService.applyControlToSimulation(deviceCommand);

                        log.info(
                                        "consumedeviceCommand: successfully applied control command to simulation for siteId={}",
                                        deviceCommand.getSiteId());
                } catch (DuplicateCommandException ex) {
                        log.warn(
                                        "Dispatch already applied. Skipping event. eventId={}, siteId={}",
                                        deviceCommand.getEventId(),
                                        deviceCommand.getSiteId());
                }

        }

        @KafkaListener(topics = "${property.config.kafka.site-creation-topic}", groupId = "${property.config.kafka.site-creation-group-id}")
        public void consumeSiteCreationEvents(SiteCreationEvent siteCreationEvent) {

                log.info(
                                "consumeSiteCreationEvents: received site creation event from topic={} siteId={} batteryCapacityWh={}",
                                siteCreationTopic,
                                siteCreationEvent.getSiteId(),
                                siteCreationEvent.getBatteryCapacityW());

                log.debug(
                                "consumeSiteCreationEvents: payload={}",
                                siteCreationEvent);

                log.debug("consumeSiteCreationEvents: calling external service using feign client to create new meter with siteId = {} and batteryCapacity = {}",
                                siteCreationEvent.getSiteId(), siteCreationEvent.getBatteryCapacityW());
                simulatorFeignClientService.activateMeterData(
                                siteCreationEvent.getSiteId(),
                                siteCreationEvent.getBatteryCapacityW());

                log.info(
                                "consumeSiteCreationEvents: meter successfully created for siteId={}",
                                siteCreationEvent.getSiteId());
        }

}
