package com.rawsaurus.sleep_not_included.build.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BUILD_USER_DELETED_QUEUE = "build.user.deleted.queue";
    public static final String BUILD_TAG_DELETED_QUEUE = "build.tag.deleted.queue";
    public static final String USER_EVENT_EXCHANGE = "user.events";
    public static final String BUILD_EVENT_EXCHANGE = "build.events";
    public static final String TAG_EVENT_EXCHANGE = "tag.events";

    @Bean
    public FanoutExchange userEventsExchange(){
        return ExchangeBuilder
                .fanoutExchange(USER_EVENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public FanoutExchange buildEventsExchange(){
        return ExchangeBuilder
                .fanoutExchange(BUILD_EVENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public FanoutExchange tagEventsExchange(){
        return ExchangeBuilder
                .fanoutExchange(TAG_EVENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue buildUserDeletedQueue(){
        return QueueBuilder.durable(BUILD_USER_DELETED_QUEUE).build();
    }

    @Bean
    public Queue buildTagDeletedQueue(){
        return QueueBuilder.durable(BUILD_TAG_DELETED_QUEUE).build();
    }

    @Bean
    public Binding bindBuildToUserEvents(){
        return BindingBuilder
                .bind(buildUserDeletedQueue())
                .to(userEventsExchange());
    }

    @Bean
    public Binding bindBuildToTagEvents(){
        return BindingBuilder
                .bind(buildTagDeletedQueue())
                .to(tagEventsExchange());
    }

    @Bean
    public MessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
