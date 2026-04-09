package com.project.hems.payment_service.service;

import com.project.hems.payment_service.entity.MeterReading;
import com.project.hems.payment_service.repository.MeterReadingRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Kafka Consumer for Raw Energy Data
 *
 * Listens to 'raw-energy-topic' topic from Simulator Service
 * and stores meter readings in database
 */
@Slf4j
@Service
public class MeterDataConsumer {

    private final MeterReadingRepository meterReadingRepository;
    private final ObjectMapper objectMapper;

    public MeterDataConsumer(
            MeterReadingRepository meterReadingRepository,
            ObjectMapper objectMapper
    ) {
        this.meterReadingRepository = meterReadingRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Consumes meter snapshot messages from Kafka
     *
     * Expected message format (JSON):
     * {
     *   "siteId": 1,
     *   "solarGeneratedKwh": 50.5,
     *   "gridImportedKwh": 10.2,
     *   "gridExportedKwh": 5.0,
     *   "timestamp": "2024-03-27T10:30:00"
     * }
     *
     * @param message JSON message from Kafka
     */
    @KafkaListener(
            topics = "raw-energy-topic",
            groupId = "payment-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMeterSnapshot(String message) {
        try {
            log.info("[PAYMENT-KAFKA] Received meter snapshot: {}", message);

            // Parse JSON message
            Map<String, Object> data = objectMapper.readValue(message, Map.class);

            Long siteId = Long.parseLong(data.get("siteId").toString());
            Double solarGenerated = Double.parseDouble(data.get("solarGeneratedKwh").toString());
            Double gridImported = Double.parseDouble(data.get("gridImportedKwh").toString());
            Double gridExported = Double.parseDouble(
                    data.getOrDefault("gridExportedKwh", 0.0).toString()
            );

            // Create meter reading record
            MeterReading reading = new MeterReading();
            reading.setSiteId(siteId);
            reading.setSolarYieldKwh(BigDecimal.valueOf(solarGenerated));
            reading.setGridImportKwh(BigDecimal.valueOf(gridImported));
            reading.setGridExportKwh(BigDecimal.valueOf(gridExported));
            reading.setReadingDate(LocalDate.now());
            reading.setProcessed(false);  // Will be processed later

            // Save to database
            MeterReading savedReading = meterReadingRepository.save(reading);

            log.info(
                    "[PAYMENT-KAFKA] ✓ Meter reading saved. Site: {}, Solar: {} kWh, Grid Import: {} kWh",
                    siteId, solarGenerated, gridImported
            );

        } catch (Exception e) {
            log.error("[PAYMENT-KAFKA] ✗ Error processing meter snapshot: {}", message, e);
            // In production, you might want to send to DLQ (Dead Letter Queue)
        }
    }

    /**
     * Alternative consumer for batch processing
     * Useful if you want to process multiple readings at once
     */
    @KafkaListener(
            topics = "meter-snapshots-batch",
            groupId = "payment-service-batch-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMeterSnapshotBatch(String message) {
        try {
            log.info("[PAYMENT-KAFKA-BATCH] Processing batch message");
            // Future: Handle batch processing
        } catch (Exception e) {
            log.error("[PAYMENT-KAFKA-BATCH] Error processing batch", e);
        }
    }
}
