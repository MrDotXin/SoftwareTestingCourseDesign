package com.mrdotxin.propsmart.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrdotxin.propsmart.model.dto.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务
 * 处理实时消息推送
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketService {

    /**
     * 用于存储每个用户的WebSocketService实例
     * 以userId为key，WebSocketService实例为value
     */
    private static final Map<String, WebSocketService> clients = new ConcurrentHashMap<>();

    /**
     * 管理员角色ID的集合
     */
    private static final Integer ADMIN_ROLE_ID = 1;  // 假设管理员角色ID为1

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 当前会话
     */
    private Session session;

    /**
     * JSON转换器
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 连接建立时调用
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        this.userId = userId;
        this.session = session;
        clients.put(userId, this);
        log.info("WebSocket连接建立，用户ID：{}", userId);
    }

    /**
     * 连接关闭时调用
     */
    @OnClose
    public void onClose() {
        clients.remove(this.userId);
        log.info("WebSocket连接关闭，用户ID：{}", userId);
    }

    /**
     * 收到客户端消息时调用
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到用户{}的消息: {}", this.userId, message);
    }

    /**
     * 连接发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误，用户ID：{}，错误信息：{}", this.userId, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 发送消息给指定用户
     * 
     * @param userId 用户ID
     * @param message 消息内容
     */
    public static void sendMessageToUser(String userId, WebSocketMessage message) {
        WebSocketService webSocketService = clients.get(userId);
        if (webSocketService != null) {
            try {
                webSocketService.session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
            } catch (IOException e) {
                log.error("发送消息给用户{}失败", userId, e);
            }
        }
    }

    /**
     * 发送消息给所有管理员
     * 
     * @param message 消息内容
     */
    public static void sendMessageToAllAdmins(WebSocketMessage message) {
        clients.forEach((id, client) -> {
            // 这里需要根据实际业务逻辑判断用户是否是管理员
            // 实际项目中可能需要调用用户服务接口或者查询数据库
            if (id.startsWith("admin_")) {  // 假设管理员的userId以admin_开头
                try {
                    client.session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
                } catch (IOException e) {
                    log.error("发送消息给管理员{}失败", id, e);
                }
            }
        });
    }

    /**
     * 发送消息给指定楼栋的所有用户
     * 
     * @param buildingId 楼栋ID
     * @param message 消息内容
     */
    public static void sendMessageToBuildingUsers(Long buildingId, WebSocketMessage message) {
        // 这里需要根据实际业务逻辑获取楼栋用户
        // 实际项目中可能需要调用用户服务接口或者查询数据库
        // 为了演示，暂时不实现详细逻辑
        log.info("向楼栋{}的所有用户发送消息: {}", buildingId, message);
    }

    /**
     * 获取当前在线用户数量
     * 
     * @return 在线用户数
     */
    public static int getOnlineCount() {
        return clients.size();
    }
} 