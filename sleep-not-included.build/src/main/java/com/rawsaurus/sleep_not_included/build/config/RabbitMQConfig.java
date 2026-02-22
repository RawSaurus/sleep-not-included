package com.rawsaurus.sleep_not_included.build.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String queueName = "build.entity.deleted.queue";
    public static String exchangeName = "user.events";
    public static String routingKey = "entity.deleted";

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(queueName).build();
    }

//    @Bean
//    public TopicExchange exchange(){
//        return ExchangeBuilder
//                .topicExchange(exchangeName)
//                .durable(true)
//                .build();
//    }

//    @Bean
//    public Binding binding(){
//        return BindingBuilder
//                .bind(queue())
//                .to(exchange())
//                .with(routingKey);
//    }

    @Bean
    public FanoutExchange userEventsExchange(){
        return ExchangeBuilder
                .fanoutExchange(exchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public Binding deleteImageBinding(Queue queue, FanoutExchange userEventsExchange){
        return BindingBuilder
                .bind(queue)
                .to(userEventsExchange);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }

//    @Bean
//    public RabbitTemplate template(ConnectionFactory connectionFactory){
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(messageConverter());
//        template.setExchange(exchangeName);
//        return template;
//    }
}
