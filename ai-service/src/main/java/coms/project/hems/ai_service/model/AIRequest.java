package coms.project.hems.ai_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Base request object for all AI operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIRequest implements Serializable {
    
    private String requestId;
    private String aiType;  // OPTIMIZATION, PREDICTION, ANOMALY_DETECTION, NLP, SCHEDULING
    private String prompt;
    private Map<String, Object> context;  // Additional context data
    private int maxTokens;
    private float temperature;  // 0.0 - 1.0
    private String model;  // gpt-4, gpt-3.5-turbo, ollama, etc.
    
    public static final int DEFAULT_MAX_TOKENS = 2000;
    public static final float DEFAULT_TEMPERATURE = 0.7f;
}
