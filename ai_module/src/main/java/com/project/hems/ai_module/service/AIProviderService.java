package com.project.hems.ai_module.service;

import com.project.hems.ai_module.model.AIRequest;
import com.project.hems.ai_module.model.AIResponse;

public interface AIProviderService {

    AIResponse processRequest(AIRequest request);

    String getProviderName();

    boolean isAvailable();

    String[] getSupportedModels();

    default boolean validateRequest(AIRequest request) {
        return request != null && request.getPrompt() != null && !request.getPrompt().isEmpty();
    }
}