package com.hems.ai.processor;


import com.hems.ai.model.AIRequest;
import com.hems.ai.model.AIResponse;
import com.hems.ai.service.AIProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Processor for energy optimization AI tasks
 * Analyzes energy patterns and provides recommendations for battery scheduling and solar utilization
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EnergyOptimizationProcessor {
    
    private final AIProviderService aiProviderService;
    
    /**
     * Generate energy optimization recommendations
     * @param siteId Site identifier
     * @param currentConsumption Current energy consumption in kWh
     * @param batteryCapacity Battery capacity in kWh
     * @param solarCapacity Solar capacity in kW
     * @return AI recommendations
     */
    public AIResponse getOptimizationRecommendations(String siteId, double currentConsumption,
                                                     double batteryCapacity, double solarCapacity) {
        try {
            String prompt = buildOptimizationPrompt(siteId, currentConsumption, batteryCapacity, solarCapacity);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("OPTIMIZATION")
                    .prompt(prompt)
                    .context(Map.of(
                            "siteId", siteId,
                            "currentConsumption", currentConsumption,
                            "batteryCapacity", batteryCapacity,
                            "solarCapacity", solarCapacity
                    ))
                    .maxTokens(AIRequest.DEFAULT_MAX_TOKENS)
                    .temperature(0.5f)
                    .build();
            
            AIResponse response = aiProviderService.processRequest(request);
            log.info("Energy optimization recommendations generated for site: {}", siteId);
            return response;
            
        } catch (Exception e) {
            log.error("Error generating energy optimization recommendations for site {}: {}", siteId, e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    /**
     * Recommend battery charge/discharge schedule
     */
    public AIResponse getBatterySchedule(String siteId, double batteryCapacity, 
                                         double[] hourlyDemand, double[] hourlyGeneration) {
        try {
            String prompt = buildBatterySchedulePrompt(siteId, batteryCapacity, hourlyDemand, hourlyGeneration);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("OPTIMIZATION")
                    .prompt(prompt)
                    .context(Map.of(
                            "siteId", siteId,
                            "batteryCapacity", batteryCapacity,
                            "demandProfile", hourlyDemand,
                            "generationProfile", hourlyGeneration
                    ))
                    .maxTokens(2000)
                    .temperature(0.3f)
                    .build();
            
            return aiProviderService.processRequest(request);
            
        } catch (Exception e) {
            log.error("Error generating battery schedule: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    private String buildOptimizationPrompt(String siteId, double consumption, double battery, double solar) {
        return String.format("""
                As an energy optimization expert, analyze the following site data and provide specific recommendations:
                
                Site ID: %s
                Current Consumption: %.2f kWh
                Battery Capacity: %.2f kWh
                Solar Capacity: %.2f kW
                
                Please provide:
                1. Current energy efficiency assessment
                2. Top 3 actionable recommendations to reduce consumption
                3. Optimal battery usage strategy
                4. Solar utilization improvements
                5. Estimated savings potential
                
                Format your response as JSON with clear keys and values.
                """, siteId, consumption, battery, solar);
    }
    
    private String buildBatterySchedulePrompt(String siteId, double capacity, double[] demand, double[] generation) {
        StringBuilder sb = new StringBuilder();
        sb.append("Create an optimal 24-hour battery charge/discharge schedule:\n\n");
        sb.append("Site: ").append(siteId).append("\n");
        sb.append("Battery Capacity: ").append(capacity).append(" kWh\n");
        sb.append("Hourly Demand Profile:\n");
        for (int i = 0; i < demand.length; i++) {
            sb.append(String.format("Hour %d: %.2f kWh\n", i, demand[i]));
        }
        sb.append("\nHourly Generation Profile:\n");
        for (int i = 0; i < generation.length; i++) {
            sb.append(String.format("Hour %d: %.2f kW\n", i, generation[i]));
        }
        sb.append("\nProvide a JSON schedule with hourly charge/discharge recommendations.");
        return sb.toString();
    }
}
