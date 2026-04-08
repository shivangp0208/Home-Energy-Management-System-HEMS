package coms.project.hems.ai_service.config;

import coms.project.hems.ai_service.processor.AnomalyDetectionProcessor;
import coms.project.hems.ai_service.processor.EnergyOptimizationProcessor;
import coms.project.hems.ai_service.processor.NaturalLanguageProcessor;
import coms.project.hems.ai_service.processor.PredictiveAnalyticsProcessor;
import coms.project.hems.ai_service.service.AIProviderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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