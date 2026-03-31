package com.hems.project.ADMIN_SERVICE.config;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    // ✅ GLOBAL RATE LIMIT CONFIG
    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {

            RateLimiterConfig config = RateLimiterConfig.custom()
                    .limitForPeriod(5)
                    .limitRefreshPeriod(Duration.ofSeconds(10))
                    .timeoutDuration(Duration.ZERO)
                    .build();

            RateLimiterRegistry registry = RateLimiterRegistry.ofDefaults();

            // ✅ CREATE ONCE
            registry.rateLimiter("globalApiLimiter", config);

            return registry;
        }

}