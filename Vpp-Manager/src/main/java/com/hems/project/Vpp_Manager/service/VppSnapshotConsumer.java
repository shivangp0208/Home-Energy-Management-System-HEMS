package com.hems.project.Vpp_Manager.service;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.project.hems.hems_api_contracts.contract.vpp.VppSnapshot;

@Slf4j
@Component
public class VppSnapshotConsumer {

  @Value("${property.kafka.vpp-snapshots-topic}")
  private String topic;

  @KafkaListener(topics = "${property.kafka.vpp-snapshots-topic}")
  public void consume(ConsumerRecord<String,VppSnapshot> record, Acknowledgment ack) {
    try {
      VppSnapshot snapshot = record.value();

      log.info("VPP-MANAGER ✅ got snapshot vppId={} totalGen={}W ts={}",
          snapshot.getVppId(),
          snapshot.getTotalGenerationW(),
          snapshot.getTimestamp());

      // TODO: store hot state in Redis (recommended)
      // redisService.put(snapshot.getVppId(), snapshot);

      // ack.acknowl edge();  
    } catch (Exception e) {
      log.error("VPP-MANAGER ❌ consume failed err={}", e.getMessage(), e);
      // no ack => retry
    }
  }
}
