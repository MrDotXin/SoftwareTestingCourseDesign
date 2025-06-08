package com.mrdotxin.propsmart.amqp;

import cn.hutool.core.util.ObjectUtil;
import com.mrdotxin.propsmart.config.RabbitMQConfig;
import com.mrdotxin.propsmart.websocket.WebSocketConnection;
import com.mrdotxin.propsmart.websocket.WebSocketService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class WebsocketSendConsumer {

    @Resource
    private WebSocketService webSocketService;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @RabbitListener(
            queues = RabbitMQConfig.websocketMsgQueue,
            ackMode = "MANUAL"
    )
    public void WebsocketMsgSender(String msg, Channel channel, Message message) throws IOException {
        try {
            ProcessMessage(msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("wrong message {} ", msg);
            // 进入低优先级队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.retryWebsocketMsgQueue, ackMode = "MANUAL")
    public void WebsocketRetryMsgSender(String msg, Channel channel, Message message) throws IOException {
        try {
            ProcessMessage(msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            int retry = (int) headers.get("retry");
            if (++retry > 3) {
                // 超过三次就直接强制确认, 不发送
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                headers.put("retry", retry);
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }
        }
    }

    private void ProcessMessage(String msg) throws IOException {
        String [] strings = msg.split(" ");
        Long userId = Long.parseLong(strings[0]);
        boolean isPersistent = Boolean.parseBoolean(strings[1]);
        String sendMsg = strings[2];

        WebSocketConnection connection = WebSocketConnection.getById(userId);
        if (ObjectUtil.isNull(connection)) {
            log.error("用户{}断开连接", userId);
            if (isPersistent) {
                redisTemplate.opsForList().rightPush("PropSmart:message" + userId, sendMsg);
            }
        } else {
            WebSocketConnection.sendWithTimeout(connection.getSession(), sendMsg, 5000L);
        }
    }
}
