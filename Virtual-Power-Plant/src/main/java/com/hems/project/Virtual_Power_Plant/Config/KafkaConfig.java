package com.hems.project.Virtual_Power_Plant.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topic(){
        return new NewTopic("my-topic",1,(short)1);
    }

    @Bean
    public NewTopic topic2(){
            return new NewTopic("VPP-REQUIREMENT", 1, (short)1);
    }
}

