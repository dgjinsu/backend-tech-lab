package com.example.springbootrabbitmq.infrastructure.rabbitmq.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    /**
     * 지정된 익스체인지 이름으로 DirectExchange 빈을 생성
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(rabbitMQProperties.getExchangeName());
    }

    @Bean
    public List<Queue> queues() {
        return rabbitMQProperties.getQueues().stream()
            .map(queueConfig -> new Queue(queueConfig.getName()))
            .toList();
    }

    @Bean
    public List<Binding> bindings(DirectExchange exchange) {
        return rabbitMQProperties.getQueues().stream()
            .map(queueConfig -> {
                Queue queue = new Queue(queueConfig.getName());
                return BindingBuilder.bind(queue)
                    .to(exchange)
                    .with(queueConfig.getRoutingKey());
            }).toList();
    }

    /**
     * RabbitMQ 연결을 위한 ConnectionFactory 빈을 생성하여 반환
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMQProperties.getHost());
        connectionFactory.setPort(rabbitMQProperties.getPort());
        connectionFactory.setUsername(rabbitMQProperties.getUsername());
        connectionFactory.setPassword(rabbitMQProperties.getPassword());
        return connectionFactory;
    }

    /**
     * RabbitTemplate을 생성하여 반환
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Jackson 라이브러리를 사용하여 메시지를 JSON 형식으로 변환하는 MessageConverter 빈을 생성
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
