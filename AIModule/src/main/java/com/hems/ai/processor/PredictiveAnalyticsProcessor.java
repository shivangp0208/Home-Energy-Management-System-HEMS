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
 * Processor for predictive analytics AI tasks
 * Predicts future energy demand, VPP optimization, and consumption patterns
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PredictiveAnalyticsProcessor {
    
    private final AIProviderService aiProviderService;
    
    /**
     * Predict future energy demand
     */
    public AIResponse predictEnergyDemand(String siteId, double[] historicalData, int forecastHours) {
        try {
            String prompt = buildDemandForecastPrompt(siteId, historicalData, forecastHours);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("PREDICTION")
                    .prompt(prompt)
                    .context(Map.of(
                            "siteId", siteId,
                            "historicalDataPoints", historicalData.length,
                            "forecastPeriod", forecastHours
                    ))
                    .maxTokens(1500)
                    .temperature(0.3f)
                    .build();
            
            AIResponse response = aiProviderService.processRequest(request);
            log.info("Energy demand prediction completed for site: {} for {} hours ahead", siteId, forecastHours);
            return response;
            
        } catch (Exception e) {
            log.error("Error predicting energy demand: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    /**
     * Optimize VPP dispatch schedule
     */
    public AIResponse optimizeVppDispatch(String vppId, double totalCapacity, 
                                         Map<String, Double> siteCapacities) {
        try {
            String prompt = buildVppOptimizationPrompt(vppId, totalCapacity, siteCapacities);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("SCHEDULING")
                    .prompt(prompt)
                    .context(Map.of(
                            "vppId", vppId,
                            "totalCapacity", totalCapacity,
                            "participatingSites", siteCapacities.size()
                    ))
                    .maxTokens(2000)
                    .temperature(0.4f)
                    .build();
            
            return aiProviderService.processRequest(request);
            
        } catch (Exception e) {
            log.error("Error optimizing VPP dispatch: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    /**
     * Predict consumption patterns for the next week
     */
    public AIResponse predictWeeklyPattern(String siteId, double[] last30DaysData) {
        try {
            String prompt = buildWeeklyPatternPrompt(siteId, last30DaysData);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("PREDICTION")
                    .prompt(prompt)
                    .context(Map.of(
                            "siteId", siteId,
                            "historicalDays", 30
                    ))
                    .maxTokens(1500)
                    .temperature(0.5f)
                    .build();
            
            return aiProviderService.processRequest(request);
            
        } catch (Exception e) {
            log.error("Error predicting weekly pattern: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    private String buildDemandForecastPrompt(String siteId, double[] historicalData, int hours) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze this energy consumption data and forecast the next ").append(hours).append(" hours:\n\n");
        sb.append("Site: ").append(siteId).append("\n");
        sb.append("Historical hourly consumption (last 72 hours):\n");
        
        for (int i = 0; i < Math.min(historicalData.length, 72); i++) {
            sb.append(String.format("Hour -%d: %.3f kWh\n", (72 - i), historicalData[i]));
        }
        
        sb.append("\nProvide a JSON forecast with:\n");
        sb.append("- hourlyForecast: array of predicted consumption for next ").append(hours).append(" hours\n");
        sb.append("- confidence: confidence level (0-100)\n");
        sb.append("- trend: UP/DOWN/STABLE\n");
        sb.append("- anomalies: any detected anomalies in the pattern\n");
        
        return sb.toString();
    }
    
    private String buildVppOptimizationPrompt(String vppId, double totalCapacity, Map<String, Double> sites) {
        StringBuilder sb = new StringBuilder();
        sb.append("Optimize VPP dispatch schedule:\n\n");
        sb.append("VPP ID: ").append(vppId).append("\n");
        sb.append("Total Capacity: ").append(totalCapacity).append(" kW\n");
        sb.append("Participating Sites:\n");
        
        sites.forEach((siteId, capacity) -> 
                sb.append(String.format("- %s: %.2f kW\n", siteId, capacity))
        );
        
        sb.append("\nProvide optimization recommendations including:\n");
        sb.append("- load_distribution: How to distribute load across sites\n");
        sb.append("- peak_shaving: Strategy to reduce peak demand\n");
        sb.append("- flexibility_score: Sites with highest flexibility\n");
        
        return sb.toString();
    }
    
    private String buildWeeklyPatternPrompt(String siteId, double[] last30Days) {
        return String.format("""
                Analyze the 30-day consumption pattern and predict the next week:
                
                Site: %s
                Last 30 days data points: %d
                Average daily consumption: %.2f kWh
                
                Please provide:
                1. Weekly pattern prediction (7 days)
                2. Day-of-week insights
                3. Peak hours identification
                4. Anomaly detection
                5. Confidence level for predictions
                
                Format as JSON.
                """, siteId, last30Days.length, calculateAverage(last30Days));
    }
    
    private double calculateAverage(double[] data) {
        double sum = 0;
        for (double d : data) {
            sum += d;
        }
        return sum / data.length;
    }
}
