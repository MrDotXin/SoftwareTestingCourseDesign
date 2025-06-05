package com.mrdotxin.propsmart.service;

import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;

/**
 * 电梯通知服务接口
 * 处理电梯状态变化和异常情况的通知
 */
public interface ElevatorNotificationService {

    /**
     * 处理电梯状态变化通知
     * @param elevator 电梯信息
     * @param prevStatus 原状态
     * @param newStatus 新状态
     */
    void handleStatusChangeNotification(Elevator elevator, String prevStatus, String newStatus);
    
    /**
     * 处理电梯异常通知
     * @param elevator 电梯信息
     * @param abnormality 异常信息
     */
    void handleAbnormalityNotification(Elevator elevator, ElevatorAbnormality abnormality);
    
    /**
     * 处理电梯维护完成通知
     * @param elevator 电梯信息
     * @param resolvedIssuesCount 解决的问题数量
     */
    void handleMaintenanceCompletedNotification(Elevator elevator, int resolvedIssuesCount);
    
    /**
     * 向特定房产所有者发送电梯相关通知
     * @param elevator 电梯信息
     * @param title 通知标题
     * @param content 通知内容
     * @param isUrgent 是否紧急
     */
    void notifyPropertyOwners(Elevator elevator, String title, String content, boolean isUrgent);
    
    /**
     * 向管理员发送异常级别通知
     * @param elevator 电梯信息
     * @param abnormalityLevel 异常级别
     * @param title 通知标题
     * @param content 通知内容
     */
    void notifyAdminsWithLevel(Elevator elevator, String abnormalityLevel, String title, String content);
} 