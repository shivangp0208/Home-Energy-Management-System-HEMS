package com.hems.project.Virtual_Power_Plant.Config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    public static final String MAIN_QUEUE = "email_service_queue";
    public static final String MAIN_EXCHANGE = "email_service_exchange";
    public static final String ROUTING_KEY = "email_service_routingKey";

    @Bean
    public Queue queue() {
        return new Queue(MAIN_QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(MAIN_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter mc) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(mc);
        return template;
    }
}