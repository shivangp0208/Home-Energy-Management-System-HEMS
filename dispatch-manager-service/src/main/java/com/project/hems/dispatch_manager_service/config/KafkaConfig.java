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

    private String dispatchEnergyTopic;
    private Integer dispatchEnergyPartitionCount;
    private String vppServiceTopic;
    private Integer vppServicePartitionCount;
    private Integer replicaCount;

    @Bean
    public NewTopic dispatchEventProducers() {
        return TopicBuilder.name(dispatchEnergyTopic)
                .partitions(dispatchEnergyPartitionCount)
                .replicas(replicaCount)
                .build();
    }


    @Bean
    public NewTopic vppReuirementProducer() {
        return TopicBuilder.name(vppServiceTopic)
                .partitions(vppServicePartitionCount)
                .replicas(replicaCount)
                .build();
    }
}
