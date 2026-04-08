package coms.project.hems.ai_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import coms.project.hems.ai_service.dto.EnergyOptimizationResult;
import coms.project.hems.ai_service.model.AIRequest;
import coms.project.hems.ai_service.model.AIResponse;
import coms.project.hems.ai_service.service.AIProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
@Slf4j
public class GeminiProviderService implements AIProviderService {

    //@Value("${spring.ai.gemini.api-key}")
    private String apiKey="AIzaSyD4xqmJU-rWIaIvQmsVm43eX8NO5S7Eq9Q";

    @Value("${spring.ai.gemini.model:gemini-2.5-flash}")
    private String model;

    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public AIResponse processRequest(AIRequest request) {

        long start = System.currentTimeMillis();

        try {
            String prompt = request.getPrompt();

            String body = """
        {
          "contents": [{
            "parts": [{"text": "%s"}]
          }]
        }
        """.formatted(prompt);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s"
                                    .formatted(model, apiKey)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            long time = System.currentTimeMillis() - start;

            // ✅ IMPORTANT PART STARTS HERE

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response.body());

            // ✅ Extract actual AI text
            String aiText = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // ✅ Clean ```json
            aiText = aiText.replace("```json", "")
                    .replace("```", "")
                    .trim();

            // ✅ Convert to your DTO
            EnergyOptimizationResult result =
                    mapper.readValue(aiText, EnergyOptimizationResult.class);

            // ✅ Return clean response
            return AIResponse.success(
                    UUID.randomUUID().toString(),
                    mapper.writeValueAsString(result),
                    model,
                    estimateTokens(aiText),
                    time
            );

        } catch (Exception e) {
            log.error("Gemini Error: {}", e.getMessage());
            return AIResponse.failure("error", e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String[] getSupportedModels() {
        return new String[]{"gemini-pro"};
    }
    private int estimateTokens(String text) {
        if (text == null) return 0;
        return text.length() / 4; // rough estimation
    }
}