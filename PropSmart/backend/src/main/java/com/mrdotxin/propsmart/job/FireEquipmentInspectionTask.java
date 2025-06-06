package com.mrdotxin.propsmart.job;

import com.mrdotxin.propsmart.model.entity.FireEquipment;
import com.mrdotxin.propsmart.service.FireEquipmentService;
import com.mrdotxin.propsmart.websocket.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 消防设备巡检定时任务
 * 负责自动检查需要巡检的消防设备并发送通知
 */
@Service
@Slf4j
public class FireEquipmentInspectionTask {

    @Resource
    private FireEquipmentService fireEquipmentService;

    @Resource
    private NotificationService notificationService;

    /**
     * 每天早上8点检查需要巡检的消防设备
     * 检查逾期以及即将到期的设备（3天内）
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkInspectionDue() {
        log.info("开始检查消防设备巡检状态...");
        
        // 获取3天内需要巡检的设备
        List<FireEquipment> equipmentList = fireEquipmentService.getEquipmentNeedingInspection(3);
        
        // 当前时间
        Date now = new Date();
        
        int overdue = 0;
        int upcoming = 0;
        
        for (FireEquipment equipment : equipmentList) {
            Date nextDue = equipment.getNextInspectionDue();
            
            // 判断是否已过期
            boolean isOverdue = nextDue != null && nextDue.before(now);
            
            if (isOverdue) {
                // 已过期，更新状态为需要巡检
                fireEquipmentService.updateEquipmentStatus(equipment.getId(), "needs_inspection");
                overdue++;
            } else {
                // 即将过期，不修改状态
                upcoming++;
            }
            
            // 发送通知
            notificationService.handleFireEquipmentInspectionNotification(equipment, isOverdue);
        }
        
        log.info("消防设备巡检检查完成: 已逾期 {} 个，即将到期 {} 个", overdue, upcoming);
    }
    
    /**
     * 每周一早上9点检查设备状态，生成巡检报告
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    public void generateWeeklyReport() {
        log.info("开始生成消防设备周报...");
        
        // 统计各种状态的设备数量
        long normalCount = fireEquipmentService.countByStatus("normal");
        long needsInspectionCount = fireEquipmentService.countByStatus("needs_inspection");
        long faultyCount = fireEquipmentService.countByStatus("faulty");
        
        // 记录到日志
        log.info("消防设备周报: 正常设备 {} 个, 需要巡检 {} 个, 故障设备 {} 个", 
                normalCount, needsInspectionCount, faultyCount);
        
        // TODO: 可以在这里添加发送周报邮件或其他通知的逻辑
    }
} 