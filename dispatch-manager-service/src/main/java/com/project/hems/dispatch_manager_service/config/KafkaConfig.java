package com.project.hems.dispatch_manager_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "property.config.kafka")
@Setter
public class KafkaConfig {

    private String dispatchCommandTopic;
    private String dispatchCommandDltTopic;
    private Integer dispatchCommandPartitionCount;
    private String dispatchEventTopic;
    private Integer dispatchEventPartitionCount;
    private String vppServiceTopic;
    private Integer vppServicePartitionCount;
    private Integer replicaCount;

    @Bean
    public NewTopic dispatchCommandTopic() {
        return TopicBuilder.name(dispatchCommandTopic)
                .partitions(dispatchCommandPartitionCount)
                .replicas(replicaCount)
                .build();
    }

    @Bean
    public NewTopic dispatchEventTopic() {
        return TopicBuilder.name(dispatchEventTopic)
                .partitions(dispatchEventPartitionCount)
                .replicas(replicaCount)
                .build();
    }

    @Bean
    public NewTopic vppReuirementTopic() {
        return TopicBuilder.name(vppServiceTopic)
                .partitions(vppServicePartitionCount)
                .replicas(replicaCount)
                .build();
    }
}
