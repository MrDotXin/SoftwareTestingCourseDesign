package com.mrdotxin.propsmart.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String websocketMsgExchange = "websocketMsgExchange";
    public static final String websocketMsgQueue = "websocketMsgQueue";
    public static final String websocketMsgRoutingKey = "websocketMsgRoutingKey";

    public static final String retryWebsocketMsgExchange = "retryWebsocketMsgExchange";
    public static final String retryWebsocketMsgQueue = "retryWebsocketMsgQueue";
    public static final String retryWebsocketMsgRoutingKey = "retryWebsocketMsgRoutingKey";
    public static final Long retryCount = 1000 * 10L;

    @Bean
    public DirectExchange getWebsocketMsgExchange() {
        return new DirectExchange(websocketMsgExchange);
    }

    @Bean
    public Queue getWebsocketMsgQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", retryWebsocketMsgExchange);
        args.put("x-dead-letter-routing-key", retryWebsocketMsgRoutingKey);
        args.put("x-priority", 5);
        return new Queue(websocketMsgQueue, true, false, false, args);
    }

    @Bean
    public Binding getWebsocketMsgBinding() {
        return BindingBuilder.bind(getWebsocketMsgQueue()).to(getWebsocketMsgExchange()).with(websocketMsgRoutingKey);
    }

    // 重试队列
    @Bean
    public DirectExchange getRetryWebsocketMsgExchange() {
        return new DirectExchange(retryWebsocketMsgExchange);
    }

    @Bean
    public Queue getRetryWebsocketMsgQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", retryCount);
        args.put("x-priority", 1);
        return new Queue(retryWebsocketMsgQueue, true, false, false, args);
    }

    @Bean
    public Binding bindRetryWebsocketMsgBinding() {
        return BindingBuilder.bind(getRetryWebsocketMsgQueue())
                .to(getRetryWebsocketMsgExchange())
                .with(retryWebsocketMsgRoutingKey);
    }

}
