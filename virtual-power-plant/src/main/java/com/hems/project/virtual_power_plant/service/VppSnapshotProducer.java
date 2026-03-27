package com.hems.project.virtual_power_plant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.vpp.VppSnapshot;

@Slf4j
@Service
@RequiredArgsConstructor
public class VppSnapshotProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Value("${property.config.kafka.vpp-snapshots-topic}")
  private String topic;

  public void publish(VppSnapshot snapshot) {
    String key = snapshot.getVppId().toString();

    kafkaTemplate.send(topic, key, snapshot)
        .whenComplete((res, ex) -> {
          if (ex != null) {
            log.error("Kafka publish FAILED vppId={} err={}", key, ex.getMessage(), ex);
          } else {
            log.debug("Kafka publish OK topic={} partition={} offset={}",
                res.getRecordMetadata().topic(),
                res.getRecordMetadata().partition(),
                res.getRecordMetadata().offset());
          }
        });
  }
}
