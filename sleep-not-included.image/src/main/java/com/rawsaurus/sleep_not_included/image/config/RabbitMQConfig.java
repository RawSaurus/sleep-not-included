package com.rawsaurus.sleep_not_included.image.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String IMAGE_USER_DELETED_QUEUE = "image.user.deleted.queue";
    public static final String IMAGE_BUILD_DELETED_QUEUE = "image.build.deleted.queue";
    public static final String USER_EVENTS_EXCHANGE = "user.events";
    public static final String BUILD_EVENTS_EXCHANGE = "build.events";
    public static final String ROUTING_KEY = "entity.deleted";

    @Bean
    public FanoutExchange userEventsExchange(){
        return ExchangeBuilder
                .fanoutExchange(USER_EVENTS_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public FanoutExchange buildEventsExchange(){
        return ExchangeBuilder
                .fanoutExchange(BUILD_EVENTS_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue imageUserDeletedQueue(){
        return QueueBuilder.durable(IMAGE_USER_DELETED_QUEUE).build();
    }

    @Bean
    public Queue imagerBuildDeletedQueue(){
        return QueueBuilder.durable(IMAGE_BUILD_DELETED_QUEUE).build();
    }

    @Bean
    public Binding imageUserBinding(){
        return BindingBuilder
                .bind(imageUserDeletedQueue())
                .to(userEventsExchange());
    }

    @Bean
    public Binding imageBuildBinding(){
        return BindingBuilder
                .bind(imagerBuildDeletedQueue())
                .to(buildEventsExchange());
    }

    @Bean
    public MessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
