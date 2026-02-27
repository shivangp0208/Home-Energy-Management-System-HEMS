package com.hems.project.Virtual_Power_Plant.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${property.config.kafka.vpp-snapshots-topic}")
    private String topic;

    @Value("${property.config.kafka.vpp-snapshots-partitions:3}")
    private int partitions;

    @Value("${property.config.kafka.replica-count:1}")
    private int replicas;

    @Value("${property.config.kafka.dispatch-command-topic}")
    private String dispatchCommandTopic;

    @Value("${property.config.kafka.dispatch-command-topic}")
    private int dispatchCommandPartitionCount;


    @Bean
    public NewTopic topic() {
        return new NewTopic("my-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic topic2() {
        return new NewTopic("VPP-REQUIREMENT", 1, (short) 1);
    }

    @Bean
    public NewTopic dispatchTopic() {
        return TopicBuilder.name(dispatchCommandTopic)
                .partitions(dispatchCommandPartitionCount)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic vppSnapshotsTopic() {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}

