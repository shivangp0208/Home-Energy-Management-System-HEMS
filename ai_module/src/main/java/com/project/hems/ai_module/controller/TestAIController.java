package com.project.hems.ai_module.controller;

import com.project.hems.ai_module.model.AIRequest;
import com.project.hems.ai_module.model.AIResponse;
import com.project.hems.ai_module.service.AIProviderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class TestAIController {

    private final AIProviderService aiService;

    public TestAIController(AIProviderService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/test")
    public AIResponse test(@RequestBody AIRequest request) {
        return aiService.processRequest(request);
    }
}