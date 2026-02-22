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

    private String queueName = "image.entity.deleted.queue";
    private String exchangeName = "user.events";
    private String routingKey = "entity.deleted";

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

    @Bean FanoutExchange fanoutExchange(){
        return ExchangeBuilder
                .fanoutExchange(exchangeName)
                .durable(true)
                .build();
    }

//    @Bean
//    public Binding binding(){
//        return BindingBuilder
//                .bind(queue())
//                .to(exchange())
//                .with(routingKey);
//    }

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
        template.setExchange(exchangeName);
        return template;
    }
}
