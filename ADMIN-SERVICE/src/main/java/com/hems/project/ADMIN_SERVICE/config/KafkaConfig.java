package com.hems.project.ADMIN_SERVICE.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${property.config.schedule-topic}")
    private String topic;

    @Value("${property.config.schedule-topic-partition-count:3}")
    private int partitions;

    @Value("${property.config.replica-count:1}")
    private int replicas;

    @Bean
    public NewTopic scheduleTopic() {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
