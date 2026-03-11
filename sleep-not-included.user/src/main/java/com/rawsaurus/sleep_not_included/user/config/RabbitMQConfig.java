package com.rawsaurus.sleep_not_included.user.config;

import com.rabbitmq.client.AMQP;
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

    public static final String USER_EVENTS_EXCHANGE = "user.events";
    public static final String IMAGE_EVENTS_EXCHANGE = "image.events";
    public static final String IMAGE_UPDATE_ROUTING_KEY = "image.user.updated";
    public static final String USER_IMAGE_UPDATE_QUEUE = "user.image.update.queue";

    @Bean
    public TopicExchange imageUpdateTopicExchange(){
        return ExchangeBuilder
                .topicExchange(IMAGE_EVENTS_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue userImageUpdateQueue(){
        return QueueBuilder.durable(USER_IMAGE_UPDATE_QUEUE).build();
    }

    @Bean
    public Binding userImageUpdateBinding(){
        return BindingBuilder
                .bind(userImageUpdateQueue())
                .to(imageUpdateTopicExchange())
                .with(IMAGE_UPDATE_ROUTING_KEY);
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return ExchangeBuilder
                .fanoutExchange(USER_EVENTS_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public MessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setExchange(USER_EVENTS_EXCHANGE);
        return template;
    }
}
