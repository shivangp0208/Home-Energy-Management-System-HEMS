package com.hems.project.admin_service.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${property.config.kafka.dispatch-event-topic}")
    private String dispatchEventTopic;

    @Value("${property.config.kafka.dispatch-event-partition-count}")
    private int dispatchEventTopicPartitionCount;

    @Value("${property.config.kafka.replica-count}")
    private int replicas;

    @Bean
    public NewTopic dispatchEventTopic() {
        return TopicBuilder.name(dispatchEventTopic)
                .partitions(dispatchEventTopicPartitionCount)
                .replicas(replicas)
                .build();
    }
}
