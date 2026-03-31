package com.project.hems.ai_module.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard response object for all AI operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponse implements Serializable {
    
    private String requestId;
    private String response;
    private String aiType;
    private String model;
    private boolean success;
    private String errorMessage;
    private Map<String, Object> metadata;  // Additional response data
    private int tokensUsed;
    private LocalDateTime processedAt;
    private long processingTimeMs;
    
    public static AIResponse success(String requestId, String response, String model, int tokens, long processingTime) {
        return AIResponse.builder()
                .requestId(requestId)
                .response(response)
                .model(model)
                .success(true)
                .tokensUsed(tokens)
                .processingTimeMs(processingTime)
                .processedAt(LocalDateTime.now())
                .build();
    }
    
    public static AIResponse failure(String requestId, String error) {
        return AIResponse.builder()
                .requestId(requestId)
                .success(false)
                .errorMessage(error)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
