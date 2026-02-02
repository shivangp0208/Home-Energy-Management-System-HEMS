package com.hems.project.Virtual_Power_Plant.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

     @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");//ahiya message broadcast thase 
        //kafka mathi live message avse e broker ma avse first and broker ma specific aa desitnatioin per avse /topic
        registry.setApplicationDestinationPrefixes("/app");
        //this is our endpoint url means user access karvu hoy toh 
        // user hit karvu padse /app/<apda-controller-no-url>
        //so ene first /app toh nakhvu j padse
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") //this endpoint client use karse connection established karva 
                .setAllowedOriginPatterns("*") //this we * because we dont no use host and port so ..
                .withSockJS(); //this is fallback method becuase some browser dont support websocket 
    }

   
}


//registry.enableSimpleBroker("/topic"); this is for broadcasting messaging
//registry.setApplicationDestinationPrefixes("/app"); and this is for client to access our endpoint