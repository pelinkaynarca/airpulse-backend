package com.airpulse.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name:air-quality-measurements}")
    private String queueName;

    @Value("${rabbitmq.exchange.name:air-quality-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:air-quality.measurement}")
    private String routingKey;

    @Bean
    public Queue measurementQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-message-ttl", 60000) // 60 seconds TTL
                .build();
    }

    @Bean
    public Exchange measurementExchange() {
        return ExchangeBuilder.directExchange(exchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public Binding measurementBinding(Queue measurementQueue, Exchange measurementExchange) {
        return BindingBuilder
                .bind(measurementQueue)
                .to(measurementExchange)
                .with(routingKey)
                .noargs();
    }

    // use JSON converter for serialization issues
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}