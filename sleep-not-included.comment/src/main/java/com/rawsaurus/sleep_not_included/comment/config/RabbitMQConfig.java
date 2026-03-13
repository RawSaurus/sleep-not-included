package com.rawsaurus.sleep_not_included.comment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COMMENT_USER_DELETED_QUEUE = "comment.user.deleted.queue";
    public static final String COMMENT_BUILD_DELETED_QUEUE = "comment.build.deleted.queue";
    public static final String USER_EVENTS_EXCHANGE = "user.events";
    public static final String BUILD_EVENTS_EXCHANGE = "build.events";

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
    public Queue commentUserDeletedQueue(){
        return QueueBuilder.durable(COMMENT_USER_DELETED_QUEUE).build();
    }

    @Bean
    public Queue commentBuildDeletedQueue(){
        return QueueBuilder.durable(COMMENT_BUILD_DELETED_QUEUE).build();
    }

    @Bean
    public Binding commentUserBinding(){
        return BindingBuilder
                .bind(commentUserDeletedQueue())
                .to(userEventsExchange());
    }

    @Bean
    public Binding commentBuildBinding(){
        return BindingBuilder
                .bind(commentBuildDeletedQueue())
                .to(buildEventsExchange());
    }

    @Bean
    public MessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
