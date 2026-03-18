package com.project.hems.chatbot_service.web.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder clientBuilder) {
        this.chatClient = clientBuilder.build();
    }

    @GetMapping(path = "/api")
    @ResponseStatus(HttpStatus.OK)
    public String getPrompt(@RequestParam("prompt") String prompt) {
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}
