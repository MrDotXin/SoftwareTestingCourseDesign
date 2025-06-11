package com.mrdotxin.propsmart.websocket;

import cn.hutool.json.JSONUtil;
import com.mrdotxin.propsmart.amqp.WebsocketSendPublisher;
import com.mrdotxin.propsmart.model.dto.WebSocketMessage;
import com.mrdotxin.propsmart.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class WebSocketService {

    @Resource
    private UserService userService;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private WebsocketSendPublisher websocketSendPublisher;

    /**
     * 发送并清除用户的所有缓存消息
     * @param userId 用户ID
     */
    public void sendCachedMessage(Long userId) {
        String key = "PropSmart:message:" + userId;

        // 原子化操作：读取并删除
        List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
        if (messages != null && !messages.isEmpty()) {
            messages.forEach(websocketSendPublisher::sendMessage);
            redisTemplate.delete(key); // 删除整个列表
        }
    }
    /**
     * 发送消息给指定用户
     *
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendMessageToUser(Long userId, WebSocketMessage message, boolean persistent) {
        websocketSendPublisher.sendMessage(userId + " " + (persistent ? 1 : 0) + " " + JSONUtil.toJsonStr(message));
    }

    /**
     * 发送消息给所有管理员
     *
     * @param message 消息内容
     */
    public void sendMessageToAll(WebSocketMessage message, boolean persistent) {
        if (!persistent) {
            Set<Long> userIds = WebSocketConnection.getMap().keySet();
            userIds.forEach(id -> {
                this.sendMessageToUser(id, message, false);
            });
        } else {
            List<Long> userIds = userService.listUserIdAll();
            userIds.forEach(id -> {
                this.sendMessageToUser(id, message, true);
            });
        }
    }

        /**
     * 发送消息给所有管理员
     *
     * @param message 消息内容
     */
    public void sendMessageToAllAdmins(WebSocketMessage message, boolean persistent) {
        List<Long> userIds = userService.listAdminId();
        userIds.forEach(id -> {
            this.sendMessageToUser(id, message, persistent);
        });
    }

    /**
     * 发送消息给指定楼栋的所有用户
     *
     * @param buildingId 楼栋ID
     * @param message 消息内容
     */
    public void sendMessageToBuildingUsers(Long buildingId, WebSocketMessage message, boolean persistent) {
        List<Long> list = userService.listUserIdByBuildingId(buildingId);
        list.forEach(t -> this.sendMessageToUser(t, message, persistent));

        log.info("向楼栋{}的所有用户发送消息: {}", buildingId, message);
    }

}
