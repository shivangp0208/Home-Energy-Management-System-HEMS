package com.hems.project.virtual_power_plant.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "property.config.kafka.vpp-snapshots-topic")
public class VppKafkaProps {
    private String vppEnergyTopic;
}
