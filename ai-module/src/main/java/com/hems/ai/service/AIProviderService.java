package com.hems.ai.service;

import com.hems.ai.model.AIRequest;
import com.hems.ai.model.AIResponse;

public interface AIProviderService {

    AIResponse processRequest(AIRequest request);

    String getProviderName();

    boolean isAvailable();

    String[] getSupportedModels();

    default boolean validateRequest(AIRequest request) {
        return request != null && request.getPrompt() != null && !request.getPrompt().isEmpty();
    }
}