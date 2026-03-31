package com.project.hems.ai_module.processor;

import com.project.hems.ai_module.model.AIRequest;
import com.project.hems.ai_module.model.AIResponse;
import com.project.hems.ai_module.service.AIProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Processor for anomaly detection AI tasks
 * Detects unusual patterns in energy consumption, dispatch events, and system behavior
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AnomalyDetectionProcessor {
    
    private final AIProviderService aiProviderService;
    
    /**
     * Detect anomalies in energy consumption patterns
     */
    public AIResponse detectConsumptionAnomalies(String siteId, double[] consumptionData,
                                                 double[] expectedPattern) {
        try {
            String prompt = buildAnomalyPrompt(siteId, consumptionData, expectedPattern);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("ANOMALY_DETECTION")
                    .prompt(prompt)
                    .context(Map.of(
                            "siteId", siteId,
                            "dataPoints", consumptionData.length,
                            "deviation", calculateDeviation(consumptionData, expectedPattern)
                    ))
                    .maxTokens(1500)
                    .temperature(0.3f)
                    .build();
            
            AIResponse response = aiProviderService.processRequest(request);
            log.info("Anomaly detection completed for site: {}", siteId);
            return response;
            
        } catch (Exception e) {
            log.error("Error detecting consumption anomalies: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    /**
     * Detect unusual dispatch patterns
     */
    public AIResponse detectDispatchAnomalies(String vppId, int[] dispatchFrequency, 
                                             double[] responseTime) {
        try {
            String prompt = buildDispatchAnomalyPrompt(vppId, dispatchFrequency, responseTime);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("ANOMALY_DETECTION")
                    .prompt(prompt)
                    .context(Map.of(
                            "vppId", vppId,
                            "dispatchEvents", dispatchFrequency.length,
                            "avgResponseTime", calculateAverage(responseTime)
                    ))
                    .maxTokens(1500)
                    .temperature(0.4f)
                    .build();
            
            return aiProviderService.processRequest(request);
            
        } catch (Exception e) {
            log.error("Error detecting dispatch anomalies: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    /**
     * Detect system-level anomalies
     */
    public AIResponse detectSystemAnomalies(Map<String, Object> systemMetrics) {
        try {
            String prompt = buildSystemAnomalyPrompt(systemMetrics);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("ANOMALY_DETECTION")
                    .prompt(prompt)
                    .context(systemMetrics)
                    .maxTokens(1500)
                    .temperature(0.3f)
                    .build();
            
            return aiProviderService.processRequest(request);
            
        } catch (Exception e) {
            log.error("Error detecting system anomalies: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    private String buildAnomalyPrompt(String siteId, double[] actual, double[] expected) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze consumption data for anomalies:\n\n");
        sb.append("Site: ").append(siteId).append("\n");
        sb.append("Data points analyzed: ").append(actual.length).append("\n\n");
        sb.append("Actual vs Expected consumption (last 24 hours):\n");
        
        for (int i = 0; i < Math.min(actual.length, 24); i++) {
            double deviation = Math.abs(actual[i] - expected[i]);
            double percentDeviation = (deviation / expected[i]) * 100;
            sb.append(String.format("Hour %d: Actual=%.2f, Expected=%.2f, Deviation=%+.1f%%\n", 
                    i, actual[i], expected[i], percentDeviation));
        }
        
        sb.append("\nDetect anomalies and provide:\n");
        sb.append("- anomalies: list of detected anomalies\n");
        sb.append("- severity: HIGH/MEDIUM/LOW\n");
        sb.append("- likely_cause: possible explanation\n");
        sb.append("- recommendation: suggested action\n");
        
        return sb.toString();
    }
    
    private String buildDispatchAnomalyPrompt(String vppId, int[] frequency, double[] responseTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze VPP dispatch patterns for anomalies:\n\n");
        sb.append("VPP: ").append(vppId).append("\n");
        sb.append("Dispatch frequency (last 30 days): ");
        for (int f : frequency) {
            sb.append(f).append(" ");
        }
        sb.append("\nAverage response time: ").append(calculateAverage(responseTime)).append("ms\n");
        
        sb.append("\nIdentify anomalies such as:\n");
        sb.append("- Unusual dispatch patterns\n");
        sb.append("- Slow response times\n");
        sb.append("- Participation rate changes\n");
        sb.append("- Failed dispatch events\n");
        
        return sb.toString();
    }
    
    private String buildSystemAnomalyPrompt(Map<String, Object> metrics) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze system health for anomalies:\n\n");
        metrics.forEach((key, value) -> 
                sb.append(String.format("- %s: %s\n", key, value))
        );
        
        sb.append("\nProvide:\n");
        sb.append("- health_status: CRITICAL/WARNING/HEALTHY\n");
        sb.append("- anomalies: any detected issues\n");
        sb.append("- recommendations: suggested remediation\n");
        
        return sb.toString();
    }
    
    private double calculateDeviation(double[] actual, double[] expected) {
        double totalDeviation = 0;
        for (int i = 0; i < Math.min(actual.length, expected.length); i++) {
            totalDeviation += Math.abs(actual[i] - expected[i]);
        }
        return totalDeviation / Math.min(actual.length, expected.length);
    }
    
    private double calculateAverage(double[] data) {
        double sum = 0;
        for (double d : data) {
            sum += d;
        }
        return sum / data.length;
    }
}
