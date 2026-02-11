package com.project.hems.simulator_service.service;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;
import com.project.hems.simulator_service.config.ActiveControlStore;
import com.project.hems.simulator_service.domain.MeterEntity;
import com.project.hems.simulator_service.model.ActiveControlState;
import com.project.hems.simulator_service.repository.MeterRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "property.config.kafka")
public class MeterSimulationService {

        private final Map<UUID, MeterSnapshot> meterReadings;
        private final MeterManagementService meterManagementService;
        private final MeterRepository meterRepository;
        private final ModelMapper mapper;
        private final KafkaTemplate<String, Object> kafkaTemplate;
        private final EnergyPhysicsEngine energyPhysicsEngine;
        private final EnvironmentSimulator environmentSimulator;
        private final ActiveControlStore activeControlStore;

        private String rawEnergyTopic;

        @Scheduled(fixedRate = 60000)
        public void saveMeterSnapshotToDB() {
                log.debug("saveMeterSnapshotToDB: scheduler triggered");

                if (meterReadings.isEmpty()) {
                        log.warn("saveMeterSnapshotToDB: map is empty, returning back");
                        return;
                }

                log.info("saveMeterSnapshotToDB: storing {} meters", meterReadings.size());

                for (Map.Entry<UUID, MeterSnapshot> entry : meterReadings.entrySet()) {

                        MeterSnapshot meter = entry.getValue();

                        meterRepository.save(mapper.map(meter, MeterEntity.class));
                }
        }

        @Scheduled(fixedRate = 5000)
        public void simulateLiveReadings() {

                log.debug("simulateLiveReadings: scheduler triggered");

                if (meterReadings.isEmpty()) {
                        log.warn(
                                        "simulateLiveReadings: meterReadings map is empty, loading initial values from DB");
                        meterManagementService.getValuesFromDB();
                        return;
                }

                log.info(
                                "simulateLiveReadings: starting simulation cycle for {} meters",
                                meterReadings.size());

                for (Map.Entry<UUID, MeterSnapshot> entry : meterReadings.entrySet()) {

                        UUID siteId = entry.getKey();
                        MeterSnapshot meter = entry.getValue();

                        log.debug(
                                        "simulateLiveReadings: simulating meter for siteId={}, meterId={}",
                                        siteId,
                                        meter.getMeterId());

                        // 1. Environmental Inputs
                        double solarW = environmentSimulator.calculateSolarProduction();
                        double loadW = environmentSimulator.calculateHomeConsumption();

                        log.debug(
                                        "simulateLiveReadings: siteId={} solarW={}W loadW={}W",
                                        siteId,
                                        solarW,
                                        loadW);

                        // 2. Physics Engine (Priority Logic)
                        Optional<ActiveControlState> activeControl = activeControlStore.getActiveControl(siteId);

                        List<EnergyPriority> loadpriorities = activeControl.map(ActiveControlState::getLoadEnergyPriorities)
                                        .orElse(ActiveControlStore.loadEnergyPriorities);
                        List<EnergyPriority> surpluspriorities = activeControl.map(ActiveControlState::getSurplusEnergyPriorities)
                                        .orElse(ActiveControlStore.surplusEnergyPriorities);
                                        
                        meter.setLoadEnergyPriorities(loadpriorities);
                        meter.setSurplusEnergyPriorities(surpluspriorities);

                        energyPhysicsEngine.processEnergyBalance(
                                        meter,
                                        solarW,
                                        loadW,
                                        loadpriorities,
                                        surpluspriorities,
                                        activeControl.orElse(null));

                        log.debug(
                                        "simulateLiveReadings: siteId={} after physics batteryPowerW={} gridPowerW={}",
                                        siteId,
                                        meter.getBatteryPowerW(),
                                        meter.getGridPowerW());

                        // 3. Electrical Noise (Voltage/Amps for realism)
                        environmentSimulator.applyElectricalMetadata(meter);

                        boolean invalidCapacity = meter.getBatteryCapacityWh() <= 0;

                        if (invalidCapacity) {
                                log.warn(
                                                "simulateLiveReadings: siteId={} invalid batteryCapacityWh={}, forcing SOC=0",
                                                siteId,
                                                meter.getBatteryCapacityWh());
                        }

                        meter.setBatterySoc(
                                        invalidCapacity
                                                        ? 0
                                                        : (int) Math.round(
                                                                        (meter.getBatteryRemainingWh()
                                                                                        / meter.getBatteryCapacityWh())
                                                                                        * 100));

                        meter.setTimestamp(LocalDateTime.now());

                        log.debug(
                                        "simulateLiveReadings: siteId={} SOC={}%, voltage={}V current={}A",
                                        siteId,
                                        meter.getBatterySoc(),
                                        meter.getCurrentVoltage(),
                                        meter.getCurrentAmps());

                        log.debug(
                                        "simulateLiveReadings: publishing meter snapshot to Kafka topic={}",
                                        rawEnergyTopic);

                        kafkaTemplate.send(rawEnergyTopic, meter);

                        log.info(
                                        "simulateLiveReadings: published snapshot siteId={} meterId={} timestamp={}",
                                        siteId,
                                        meter.getMeterId(),
                                        meter.getTimestamp());

                        log.info("live meter reading of site " + siteId);
                        log.info(meter.toString());
                        meterReadings.put(siteId, meter);
                }

                log.info("simulateLiveReadings: simulation cycle completed successfully");
        }

}
