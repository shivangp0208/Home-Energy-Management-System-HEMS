package coms.project.hems.ai_service.processor;


import coms.project.hems.ai_service.model.AIRequest;
import coms.project.hems.ai_service.model.AIResponse;
import coms.project.hems.ai_service.service.AIProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Processor for natural language query processing
 * Allows users to query program data and analytics using natural language
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NaturalLanguageProcessor {
    
    private final AIProviderService aiProviderService;
    
    /**
     * Process natural language query and convert to data query
     */
    public AIResponse processQuery(String query, Map<String, Object> context) {
        try {
            String prompt = buildNLPPrompt(query, context);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("NLP")
                    .prompt(prompt)
                    .context(context)
                    .maxTokens(1500)
                    .temperature(0.5f)
                    .build();
            
            AIResponse response = aiProviderService.processRequest(request);
            log.info("NLP query processed successfully. Query: {}", query);
            return response;
            
        } catch (Exception e) {
            log.error("Error processing NLP query: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    /**
     * Query programs with natural language
     * Example: "Show me all active programs with efficiency > 85%"
     */
    public AIResponse queryPrograms(String query, int totalPrograms, int activePrograms) {
        try {
            String prompt = buildProgramQueryPrompt(query, totalPrograms, activePrograms);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("NLP")
                    .prompt(prompt)
                    .context(Map.of(
                            "query", query,
                            "totalPrograms", totalPrograms,
                            "activePrograms", activePrograms
                    ))
                    .maxTokens(2000)
                    .temperature(0.3f)
                    .build();
            
            AIResponse response = aiProviderService.processRequest(request);
            log.info("Program query processed: {}", query);
            return response;
            
        } catch (Exception e) {
            log.error("Error processing program query: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    /**
     * Query sites with natural language
     * Example: "Which sites have low battery health and high grid dependency?"
     */
    public AIResponse querySites(String query, int totalSites, int onlineSites) {
        try {
            String prompt = buildSiteQueryPrompt(query, totalSites, onlineSites);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("NLP")
                    .prompt(prompt)
                    .context(Map.of(
                            "query", query,
                            "totalSites", totalSites,
                            "onlineSites", onlineSites
                    ))
                    .maxTokens(2000)
                    .temperature(0.3f)
                    .build();
            
            return aiProviderService.processRequest(request);
            
        } catch (Exception e) {
            log.error("Error processing site query: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    /**
     * Query VPP analytics with natural language
     * Example: "What's the dispatch performance trend for VPP-001?"
     */
    public AIResponse queryVppAnalytics(String query, String vppId) {
        try {
            String prompt = buildVppQueryPrompt(query, vppId);
            
            AIRequest request = AIRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .aiType("NLP")
                    .prompt(prompt)
                    .context(Map.of(
                            "query", query,
                            "vppId", vppId
                    ))
                    .maxTokens(2000)
                    .temperature(0.4f)
                    .build();
            
            return aiProviderService.processRequest(request);
            
        } catch (Exception e) {
            log.error("Error processing VPP analytics query: {}", e.getMessage());
            return AIResponse.failure(UUID.randomUUID().toString(), e.getMessage());
        }
    }
    
    private String buildNLPPrompt(String query, Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Process the following natural language query and provide a response:\n\n");
        sb.append("User Query: ").append(query).append("\n\n");
        
        if (!context.isEmpty()) {
            sb.append("Context Information:\n");
            context.forEach((key, value) -> 
                    sb.append("- ").append(key).append(": ").append(value).append("\n")
            );
        }
        
        sb.append("\nProvide a clear, actionable response in JSON format if possible.");
        return sb.toString();
    }
    
    private String buildProgramQueryPrompt(String query, int total, int active) {
        return String.format("""
                Analyze this program query and provide specific results:
                
                User Query: %s
                
                System Context:
                - Total Programs: %d
                - Active Programs: %d
                
                Based on this query, identify:
                1. Which programs match the criteria
                2. Key metrics for matching programs
                3. Any recommendations based on the data
                
                Format response as JSON with:
                - matched_programs: list of program IDs
                - count: number of programs matching
                - metrics: relevant performance data
                - insights: key findings
                """, query, total, active);
    }
    
    private String buildSiteQueryPrompt(String query, int total, int online) {
        return String.format("""
                Analyze this site query and provide specific results:
                
                User Query: %s
                
                System Context:
                - Total Sites: %d
                - Online Sites: %d
                - Offline Sites: %d
                
                Based on this query, identify:
                1. Sites matching the criteria
                2. Health status of matching sites
                3. Recommended actions
                
                Format response as JSON.
                """, query, total, online, (total - online));
    }
    
    private String buildVppQueryPrompt(String query, String vppId) {
        return String.format("""
                Analyze this VPP analytics query:
                
                User Query: %s
                VPP ID: %s
                
                Provide analysis including:
                1. Performance metrics
                2. Historical trends
                3. Predictions for next 7 days
                4. Optimization recommendations
                
                Format as detailed JSON response.
                """, query, vppId);
    }
}
