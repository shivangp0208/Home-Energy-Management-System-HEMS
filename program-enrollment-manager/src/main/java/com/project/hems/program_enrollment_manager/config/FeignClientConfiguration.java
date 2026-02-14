package com.project.hems.program_enrollment_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

@Configuration
public class FeignClientConfiguration {

    @Bean
    public ErrorDecoder siteFeignErrorDecoder() {
        return new FeignSiteErrorDecoder();
    }
}
