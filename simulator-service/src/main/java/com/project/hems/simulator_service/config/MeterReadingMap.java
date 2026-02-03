package com.project.hems.simulator_service.config;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;


@Configuration
public class MeterReadingMap {

    @Bean
    public Map<UUID, MeterSnapshot> getMeterMap() {
        return new ConcurrentHashMap<>();
    }
}
