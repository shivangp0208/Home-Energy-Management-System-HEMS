package com.project.hems.ai_module.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.ai")
class AIProperties {
    private OpenAIProperties openai = new OpenAIProperties();
    private OllamaProperties ollama = new OllamaProperties();
    private GeneralProperties general = new GeneralProperties();

    @Data
    static class OpenAIProperties {
        private String apiKey;
        private String model = "gpt-4-turbo";
        private Double temperature = 0.7;
        private Integer maxTokens = 2048;
    }

    @Data
    static class OllamaProperties {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama2";
    }

    @Data
    static class GeneralProperties {
        private Boolean enableAnomalyDetection = true;
        private Boolean enablePredictiveAnalytics = true;
        private Integer requestTimeoutSeconds = 30;
        private Integer maxConcurrentRequests = 10;
    }
}
