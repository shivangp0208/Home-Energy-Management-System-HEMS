package com.hems.project.Virtual_Power_Plant.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "property.config.kafka")
public class VppKafkaProps {
    private String vppEnergyTopic;
}
