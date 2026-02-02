package com.project.hems.envoy_manager_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

@Configuration
public class SimulatorFeignConfig {

    @Bean
    public ErrorDecoder simulatorErrorDecoder() {
        return new SimulatorErrorDecoder();
    }
}
