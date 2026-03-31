package com.project.hems.ai_module.config;

import com.project.hems.ai_module.processor.*;
import com.project.hems.ai_module.service.AIProviderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.project.hems.ai_module")
public class AIAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EnergyOptimizationProcessor energyOptimizationProcessor(AIProviderService aiProviderService) {
        return new EnergyOptimizationProcessor(aiProviderService);
    }

    @Bean
    @ConditionalOnMissingBean
    public PredictiveAnalyticsProcessor predictiveAnalyticsProcessor(AIProviderService aiProviderService) {
        return new PredictiveAnalyticsProcessor(aiProviderService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AnomalyDetectionProcessor anomalyDetectionProcessor(AIProviderService aiProviderService) {
        return new AnomalyDetectionProcessor(aiProviderService);
    }

    @Bean
    @ConditionalOnMissingBean
    public NaturalLanguageProcessor naturalLanguageProcessor(AIProviderService aiProviderService) {
        return new NaturalLanguageProcessor(aiProviderService);
    }
}