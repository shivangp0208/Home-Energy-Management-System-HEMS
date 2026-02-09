package com.hems.project.Virtual_Power_Plant.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hems.project.Virtual_Power_Plant.Config.VppModelMapper;
import com.hems.project.Virtual_Power_Plant.entity.VppSnapshotEntity;

import com.hems.project.Virtual_Power_Plant.repository.VppSnapshotRepository;
import com.project.hems.hems_api_contracts.contract.vpp.GenerationMode;
import com.project.hems.hems_api_contracts.contract.vpp.VppSnapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VppSimulationService {

    // HOT state (like Redis hot cache) - in-memory for dev/local
    private final Map<UUID, VppSnapshot> vppSnapshots;

    // DB (cold storage)
    private final VppSnapshotRepository vppSnapshotRepository;

    // Kafka publish (live stream)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Generation logic (your File 3)
    private final VppGenerationSimulator vppGenerationSimulator;

    // Mapper (Snapshot <-> Entity)
    private final VppModelMapper vppModelMapper;

    // put in application.yml: property.config.kafka.vppEnergyTopic
    private final VppKafkaProps vppKafkaProps;

    /**
     * Every 60 seconds persist current VPP state to DB (cold storage)
     * Like your friend's saveMeterSnapshotToDB()
     */
    //in this we save not every different raw we fetch that and that save ..

   @Scheduled(fixedRate = 60000)
public void saveVppSnapshotsToDb() {
    if (vppSnapshots.isEmpty()) return;

    try {
        List<VppSnapshotEntity> batch = vppSnapshots.values().stream()
            .map(vppModelMapper::toEntity)
            .toList();

        vppSnapshotRepository.saveAll(batch); // ✅ one batch call
        log.info("Saved {} VPP snapshots", batch.size());
    } catch (Exception e) {
        log.error("DB save failed: {}", e.getMessage(), e);
    }
}


    /**
     * Every 5 seconds simulate live readings + publish to Kafka
     * Like your friend's simulateLiveReadings()
     */
    @Scheduled(fixedRate = 5000)
    public void simulateLiveVppReadings() {

        if (vppSnapshots.isEmpty()) {
            log.warn("simulateLiveVppReadings: map is empty, trying warm-up from DB");
            warmUpFromDb();
            return;
        }

        //String topic = vppKafkaProps.getVppEnergyTopic();
        String topic="VPP_ENERGY_SNAPSHOTS";

        log.info("simulateLiveVppReadings: simulating {} VPPs", vppSnapshots.size());

        for (Map.Entry<UUID, VppSnapshot> entry : vppSnapshots.entrySet()) {
            UUID vppId = entry.getKey();
            VppSnapshot current = entry.getValue();

            try {
                // 1) simulate next snapshot state based on current snapshot
                double maxCapacityW = current.getMaxSolarCapacityW()
                        + current.getMaxCoalCapacityW()
                        + current.getMaxNuclearCapacityW()
                        + current.getMaxThermalCapacityW();

                VppSnapshot updated = vppGenerationSimulator.nextSnapshot(current, maxCapacityW);


                // 2) update timestamp
                updated.setTimestamp(LocalDateTime.now());

                // 3) publish to Kafka
                kafkaTemplate.send(topic, vppId.toString(), updated);
                log.error("TEST KAFKA SEND {}", updated);
                log.info("simulateLiveVppReadings: published vppId={} totalGen={}W grid={}W battery={}W",
                        vppId,
                        updated.getTotalGenerationW(),
                        updated.getGridPowerW(),
                        updated.getBatteryPowerW());

                // 4) update HOT map
                vppSnapshots.put(vppId, updated);

            } catch (Exception e) {
                log.error("simulateLiveVppReadings: failed simulation vppId={} error={}", vppId, e.getMessage(), e);
            }
        }
    }

    /**
     * Self-healing warm-up: load last snapshot per VPP from DB.
     * (For local dev / restart recovery)
     */
    private void warmUpFromDb() {
        try {
            var latestSnapshots = vppSnapshotRepository.findLatestSnapshotsPerVpp();

            if (latestSnapshots == null || latestSnapshots.isEmpty()) {
                log.warn("warmUpFromDb: no snapshots found in DB");
                return;
            }

            latestSnapshots.forEach(entity -> {
                VppSnapshot snapshot = vppModelMapper.toDto(entity);
                vppSnapshots.put(snapshot.getVppId(), snapshot);
                log.info("warmUpFromDb: loaded vppId={} ts={}", snapshot.getVppId(), snapshot.getTimestamp());
            });

        } catch (Exception e) {
            log.error("warmUpFromDb: failed loading snapshots error={}", e.getMessage(), e);
        }
    }

    public void startVpp(UUID vppId, double maxCapacityW, double batteryCapacityWh) {

        double solarMax = maxCapacityW * 0.5;
        double coalMax = maxCapacityW * 0.3;
        double nuclearMax = maxCapacityW * 0.15;
        double thermalMax = maxCapacityW * 0.05;

        VppSnapshot initial = VppSnapshot.builder()
                .vppId(vppId)
                .timestamp(LocalDateTime.now())
                .mode(GenerationMode.AUTO)

                .maxSolarCapacityW(solarMax)
                .maxCoalCapacityW(coalMax)
                .maxNuclearCapacityW(nuclearMax)
                .maxThermalCapacityW(thermalMax)

                .batteryCapacityWh(batteryCapacityWh)
                .batteryRemainingWh(batteryCapacityWh * 0.5)
                .batterySoc(50)

                .targetExportW(0)

                .solarW(0).coalW(0).nuclearW(0).thermalW(0)
                .totalGenerationW(0)
                .batteryPowerW(0)
                .gridPowerW(0)
                .build();

        vppSnapshots.put(vppId, initial);
    }


    public void stopVpp(UUID vppId) {
            vppSnapshots.remove(vppId);
        }

        public void setMode(UUID vppId,GenerationMode mode) {
            VppSnapshot snap = vppSnapshots.get(vppId);
            if (snap == null) return;
            snap.setMode(mode);
            vppSnapshots.put(vppId, snap);
        }

        public void setTargetExport(UUID vppId, double targetExportW) {
            VppSnapshot snap = vppSnapshots.get(vppId);
            if (snap == null) return;
            snap.setTargetExportW(targetExportW);
            vppSnapshots.put(vppId, snap);
        }

}
