package com.hems.project.Email_Service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class MessagingConfig {

    public static final String MAIN_QUEUE="email_service_queue";
    public static final String MAIN_EXCHANGE="email_service_exchange";
    public static final String ROUTING_KEY="email_service_routingKey";

    public static final String RETRY_QUEUE = "email_retry_queue";
    public static final String RETRY_EXCHANGE = "email_retry_exchange";
    public static final String RETRY_ROUTING_KEY = "email_retry";

    public static final String DLQ_QUEUE = "email_dlq";
    public static final String DLQ_EXCHANGE = "email_dlq_exchange";
    public static final String DLQ_ROUTING_KEY = "email_dlq";

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(MAIN_QUEUE)
                .withArgument("x-dead-letter-exchange", RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RETRY_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(RETRY_QUEUE)
                .withArgument("x-message-ttl", 30000)
                .withArgument("x-dead-letter-exchange", MAIN_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .build();
    }


    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(MAIN_EXCHANGE);
    }

    @Bean
    public DirectExchange retryExchange(){
        return new DirectExchange(RETRY_EXCHANGE);
    }

    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding retryBinding(Queue retryQueue, DirectExchange retryExchange){
        return BindingBuilder.bind(retryQueue).to(retryExchange).with(RETRY_ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding(Queue deadLetterQueue, DirectExchange dlqExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(dlqExchange).with(DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate=new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
