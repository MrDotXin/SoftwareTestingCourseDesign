package com.mrdotxin.propsmart.amqp;

import com.mrdotxin.propsmart.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class WebsocketSendPublisher {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.websocketMsgExchange,
                RabbitMQConfig.websocketMsgRoutingKey,
                message.getBytes(),
                correlationData
        );
    }
}
