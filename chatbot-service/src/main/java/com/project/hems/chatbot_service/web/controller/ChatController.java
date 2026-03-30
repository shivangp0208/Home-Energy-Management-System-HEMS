package com.project.hems.chatbot_service.web.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.project.hems.chatbot_service.util.PromptProvider;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    // the default window size for chat storage is 20 messages so it will be able to
    // store only 20 older messages as context
    private ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
    private PromptProvider prompt;

    public ChatController(ChatClient.Builder clientBuilder) {
        this.prompt = new PromptProvider();
        
        this.chatClient = clientBuilder
                .defaultSystem(prompt.providePrompt())
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build())
                .build();
    }

    @GetMapping(path = "/api/v1/chat")
    @ResponseStatus(HttpStatus.OK)
    public String getPrompt(@RequestParam("prompt") String prompt) {
        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
}
