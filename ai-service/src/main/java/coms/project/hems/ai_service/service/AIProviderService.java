package coms.project.hems.ai_service.service;


import coms.project.hems.ai_service.model.AIRequest;
import coms.project.hems.ai_service.model.AIResponse;

public interface AIProviderService {

    AIResponse processRequest(AIRequest request);

    String getProviderName();

    boolean isAvailable();

    String[] getSupportedModels();

    default boolean validateRequest(AIRequest request) {
        return request != null && request.getPrompt() != null && !request.getPrompt().isEmpty();
    }
}