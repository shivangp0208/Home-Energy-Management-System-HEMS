package com.hems.project.vpp_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import com.project.hems.hems_api_contracts.contract.vpp.VppSnapshot;

@RequiredArgsConstructor
@Slf4j
@Component
public class VppSnapshotConsumer {

  @Value("${property.kafka.vpp-snapshots-topic}")
  private String topic;

  private final SimpMessagingTemplate simpMessagingTemplate;
  @KafkaListener(topics = "${property.kafka.vpp-snapshots-topic}")
  public void consume(VppSnapshot record, Acknowledgment ack) {
    try {
      log.info("VPP-MANAGER got snapshot vppId={} totalGen={}W ts={}",
          record.getVppId(),
          record.getTotalGenerationW(),
          record.getTimestamp());

      simpMessagingTemplate.convertAndSend(
        "/topic/meter",
        record
      );
      ack.acknowledge();

    } catch (Exception e) {
      log.error("VPP-MANAGER consume failed err={}", e.getMessage(), e);
    }
  }
}
