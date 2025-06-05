package com.mrdotxin.propsmart.websocket;

import cn.hutool.json.JSONUtil;
import com.mrdotxin.propsmart.model.dto.WebSocketMessage;
import com.mrdotxin.propsmart.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class WebSocketService {

    @Resource
    private UserService userService;


    public void sendCachedMessage(Long userId) {

    }
    /**
     * 发送消息给指定用户
     *
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendMessageToUser(Long userId, WebSocketMessage message, boolean persistent) {
        WebSocketConnection webSocketService = WebSocketConnection.getById(userId);
        if (webSocketService != null) {
            try {
                webSocketService.getSession().getBasicRemote().sendText(JSONUtil.toJsonStr(message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 发送消息给所有管理员
     *
     * @param message 消息内容
     */
    public void sendMessageToAll(WebSocketMessage message, boolean persistent) {
        Set<Long> userIds = WebSocketConnection.getMap().keySet();
        userIds.forEach(id -> {
            this.sendMessageToUser(id, message, persistent);
        });
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
