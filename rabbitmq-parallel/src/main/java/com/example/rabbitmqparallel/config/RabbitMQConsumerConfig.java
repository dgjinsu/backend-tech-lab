package com.example.rabbitmqparallel.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConsumerConfig {

    private final ConsumerProperties consumerProperties;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        // Spring AMQP 기본값:
        // concurrentConsumers = 1
        // maxConcurrentConsumers = 1
        // prefetchCount = 250
        // acknowledgeMode = AUTO
        // defaultRequeueRejected = true

        // factory.setConcurrentConsumers(consumerProperties.getConcurrency());
        // factory.setMaxConcurrentConsumers(consumerProperties.getMaxConcurrency());
        // factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // factory.setDefaultRequeueRejected(false);

        return factory;
    }
}
