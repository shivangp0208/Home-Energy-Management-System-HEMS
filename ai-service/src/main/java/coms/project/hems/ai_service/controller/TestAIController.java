package coms.project.hems.ai_service.controller;

import coms.project.hems.ai_service.model.AIRequest;
import coms.project.hems.ai_service.model.AIResponse;
import coms.project.hems.ai_service.service.AIProviderService;
import org.springframework.web.bind.annotation.*;

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

    @RestController
    public class TestController {

        @GetMapping("/test")
        public String test(){
            return "ai-service";
        }

    }

}