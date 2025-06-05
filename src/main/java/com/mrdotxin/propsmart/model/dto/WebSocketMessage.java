package com.mrdotxin.propsmart.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * WebSocket消息数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage implements Serializable {

    /**
     * 消息类型
     * ELEVATOR: 电梯状态相关消息
     * REPAIR: 报修申请相关消息
     * BILL: 缴费查询相关消息
     * NOTICE: 公告查看相关消息
     * COMPLAINT: 投诉建议相关消息
     * VISITOR: 访客管理相关消息
     * FACILITY: 设施预订相关消息
     */
    private String type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 目标ID
     * 可能是用户ID、楼栋ID等，根据消息类型决定
     */
    private Long targetId;

    /**
     * 关联业务数据ID
     * 例如报修单ID、账单ID等
     */
    private Long businessId;

    /**
     * 是否紧急消息
     */
    private Boolean urgent;
} 