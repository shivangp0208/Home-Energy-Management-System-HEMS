package com.hems.project.Virtual_Power_Plant.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hems.project.Virtual_Power_Plant.Config.VppModelMapper;
import com.hems.project.Virtual_Power_Plant.dto.GenerationMode;
import com.hems.project.Virtual_Power_Plant.dto.VppSnapshot;
import com.hems.project.Virtual_Power_Plant.entity.VppSnapshotEntity;
import com.hems.project.Virtual_Power_Plant.repository.VppSnapshotRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VppSimulationService {

    // HOT state (in-memory cache)
    private final Map<UUID, VppSnapshot> vppSnapshots;

    // Cold storage
    private final VppSnapshotRepository vppSnapshotRepository;

    // Kafka
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Generator
    private final VppGenerationSimulator vppGenerationSimulator;

    // Mapper
    private final VppModelMapper vppModelMapper;

    // Kafka props (topic)
    private final VppKafkaProps vppKafkaProps;

    // Static config per VPP (capacity etc)
    private final Map<UUID, Double> vppMaxCapacityW = new ConcurrentHashMap<>();

    /**
     * Save snapshots to DB every 60 sec (cold storage)
     */
    @Scheduled(fixedRate = 60000)
    public void saveVppSnapshotsToDb() {
        if (vppSnapshots.isEmpty()) {
            log.warn("saveVppSnapshotsToDb: vppSnapshots map empty, nothing to save");
            return;
        }

        log.info("saveVppSnapshotsToDb: saving {} snapshots", vppSnapshots.size());

        for (Map.Entry<UUID, VppSnapshot> entry : vppSnapshots.entrySet()) {
            UUID vppId = entry.getKey();
            VppSnapshot snapshot = entry.getValue();

            try {
                VppSnapshotEntity entity = vppModelMapper.toEntity(snapshot);
                vppSnapshotRepository.save(entity);

                log.debug("saveVppSnapshotsToDb: saved vppId={} ts={}", vppId, snapshot.getTimestamp());
            } catch (Exception e) {
                log.error("saveVppSnapshotsToDb: failed vppId={} err={}", vppId, e.getMessage(), e);
            }
        }
    }

    /**
     * Simulate every 5 sec + publish to Kafka
     */
    @Scheduled(fixedRate = 5000)
    public void simulateLiveVppReadings() {

        if (vppSnapshots.isEmpty()) {
            log.warn("simulateLiveVppReadings: map empty, warm-up from DB");
            warmUpFromDb();
            return;
        }

        final String topic = vppKafkaProps.getVppEnergyTopic();

        log.info("simulateLiveVppReadings: simulating {} VPPs", vppSnapshots.size());

        for (Map.Entry<UUID, VppSnapshot> entry : vppSnapshots.entrySet()) {
            UUID vppId = entry.getKey();
            VppSnapshot current = entry.getValue();

            try {
                double maxCapacityW = vppMaxCapacityW.getOrDefault(vppId, 0.0);

                // ✅ IMPORTANT: pass capacity to simulator
                VppSnapshot updated = vppGenerationSimulator.nextSnapshot(current, maxCapacityW);

                updated.setTimestamp(LocalDateTime.now());

                // ✅ Publish to Kafka (key=vppId)
                kafkaTemplate.send(topic, vppId.toString(), updated);

                log.info("Kafka published | topic={} vppId={} totalGen={}W grid={}W battery={}W mode={}",
                        topic,
                        vppId,
                        updated.getTotalGenerationW(),
                        updated.getGridPowerW(),
                        updated.getBatteryPowerW(),
                        updated.getMode());

                // Update hot map
                vppSnapshots.put(vppId, updated);

            } catch (Exception e) {
                log.error("simulateLiveVppReadings: failed vppId={} err={}", vppId, e.getMessage(), e);
            }
        }
    }

    /**
     * Warm-up: load latest snapshot per VPP from DB
     * NOTE: DB may not have maxCapacity. For now keep 0 or set default.
     */
    private void warmUpFromDb() {
        try {
            var latestSnapshots = vppSnapshotRepository.findLatestSnapshotsPerVpp();

            if (latestSnapshots == null || latestSnapshots.isEmpty()) {
                log.warn("warmUpFromDb: no snapshots found");
                return;
            }

            latestSnapshots.forEach(entity -> {
                VppSnapshot snapshot = vppModelMapper.toDto(entity);
                vppSnapshots.put(snapshot.getVppId(), snapshot);

                // If you don't persist capacity in DB, keep default.
                vppMaxCapacityW.putIfAbsent(snapshot.getVppId(), 50000.0);

                log.info("warmUpFromDb: loaded vppId={} ts={}", snapshot.getVppId(), snapshot.getTimestamp());
            });

        } catch (Exception e) {
            log.error("warmUpFromDb: failed err={}", e.getMessage(), e);
        }
    }

    // -----------------------
    // Start / Stop / Controls
    // -----------------------

    public void startVpp(UUID vppId, double maxCapacityW, double batteryCapacityWh) {

        VppSnapshot initial = VppSnapshot.builder()
                .vppId(vppId)
                .timestamp(LocalDateTime.now())

                .solarW(0)
                .coalW(0)
                .nuclearW(0)
                .thermalW(0)
                .totalGenerationW(0)

                .batteryCapacityWh(batteryCapacityWh)
                .batteryRemainingWh(batteryCapacityWh * 0.5)
                .batterySoc(50)

                .batteryPowerW(0)
                .gridPowerW(0)

                .targetExportW(0)
                .mode(GenerationMode.AUTO)
                .build();

        vppSnapshots.put(vppId, initial);
        vppMaxCapacityW.put(vppId, maxCapacityW);

        log.info("VPP STARTED | vppId={} maxCapacityW={} batteryCapacityWh={}", vppId, maxCapacityW, batteryCapacityWh);
    }

    public void stopVpp(UUID vppId) {
        vppSnapshots.remove(vppId);
        vppMaxCapacityW.remove(vppId);
        log.info("VPP STOPPED | vppId={}", vppId);
    }

    public void setMode(UUID vppId, GenerationMode mode) {
        VppSnapshot snap = vppSnapshots.get(vppId);
        if (snap == null) return;

        snap.setMode(mode);
        vppSnapshots.put(vppId, snap);

        log.info("VPP MODE UPDATED | vppId={} mode={}", vppId, mode);
    }

    public void setTargetExport(UUID vppId, double targetExportW) {
        VppSnapshot snap = vppSnapshots.get(vppId);
        if (snap == null) return;

        snap.setTargetExportW(targetExportW);
        vppSnapshots.put(vppId, snap);

        log.info("VPP TARGET EXPORT UPDATED | vppId={} targetExportW={}", vppId, targetExportW);
    }
}
