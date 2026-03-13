package com.rawsaurus.sleep_not_included.tag.config;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TAG_EVENTS_EXCHANGE = "tag.events";

    @Bean FanoutExchange fanoutExchange(){
        return ExchangeBuilder
                .fanoutExchange(TAG_EVENTS_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public MessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setExchange(TAG_EVENTS_EXCHANGE);
        return template;
    }
}
