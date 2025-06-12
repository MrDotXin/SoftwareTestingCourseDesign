package com.mrdotxin.propsmart.service.impl.elevator;

import cn.hutool.core.date.DateUtil;
import com.mrdotxin.propsmart.model.dto.WebSocketMessage;
import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.AbnormalityLevelEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorStatusEnum;
import com.mrdotxin.propsmart.service.elevator.ElevatorNotificationService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import com.mrdotxin.propsmart.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 电梯通知服务实现
 */
@Slf4j
@Service
public class ElevatorNotificationServiceImpl implements ElevatorNotificationService {

    @Resource
    private WebSocketService webSocketService;
    
    @Resource
    private PropertyService propertyService;
    
    @Resource
    private UserService userService;

    @Override
    public void handleStatusChangeNotification(Elevator elevator, String prevStatus, String newStatus) {
        // 1. 判断状态是否发生变化
        if (prevStatus != null && prevStatus.equals(newStatus)) {
            return; // 状态未变，无需通知
        }

        // 2. 判断是否为异常状态
        boolean isAbnormal = ElevatorStatusEnum.FAULT.getStatus().equals(newStatus) 
                || ElevatorStatusEnum.WARNING.getStatus().equals(newStatus);
        
        // 3. 生成通知内容
        String userTitle;
        String userContent;
        boolean userUrgent;
        
        if (ElevatorStatusEnum.FAULT.getStatus().equals(newStatus)) {
            userTitle = "电梯故障通知";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "出现故障，当前无法使用。请选择其他电梯或楼梯。维修人员正在处理中。";
            userUrgent = true;
        } else if (ElevatorStatusEnum.WARNING.getStatus().equals(newStatus)) {
            userTitle = "电梯异常提醒";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "出现异常情况，但仍可使用。建议谨慎乘坐。";
            userUrgent = false;
        } else if (ElevatorStatusEnum.MAINTENANCE.getStatus().equals(newStatus)) {
            userTitle = "电梯维护通知";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "正在进行维护，暂时无法使用。请使用其他电梯或楼梯。";
            userUrgent = false;
        } else if (ElevatorStatusEnum.NORMAL.getStatus().equals(newStatus)) {
            userTitle = "电梯恢复正常";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "已恢复正常运行，可以正常使用。";
            userUrgent = false;
        } else {
            userTitle = "电梯状态更新";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "状态已更新为：" + newStatus + "。";
            userUrgent = false;
        }
        
        // 4. 发送用户通知
        WebSocketMessage userMessage = WebSocketMessage.builder()
                .type("ELEVATOR_STATUS")
                .title(userTitle)
                .content(userContent)
                .businessId(elevator.getId())
                .targetId(elevator.getBuildingId())
                .urgent(userUrgent)
                .build();
        
        webSocketService.sendMessageToBuildingUsers(elevator.getBuildingId(), userMessage, true);
        log.info("向楼栋{}的所有用户发送电梯状态更新: {}", elevator.getBuildingId(), userTitle);
        
        // 5. 如果是异常状态，通知管理员
        if (isAbnormal) {
            notifyAdminsAboutStatus(elevator, newStatus);
        }
    }

    @Override
    public void handleAbnormalityNotification(Elevator elevator, ElevatorAbnormality abnormality) {
        // 获取异常级别
        String abnormalityLevel = abnormality.getAbnormalityLevel();
        boolean isSerious = AbnormalityLevelEnum.SERIOUS.getLevel().equals(abnormalityLevel);
        boolean isModerate = AbnormalityLevelEnum.MODERATE.getLevel().equals(abnormalityLevel);
        
        // 1. 向管理员发送异常通知
        String adminTitle = isSerious ? "电梯严重异常报警" : (isModerate ? "电梯中等异常通知" : "电梯轻微异常提醒");
        String adminContent = createAbnormalityNotificationContent(elevator, abnormality, isSerious, isModerate);
        
        notifyAdminsWithLevel(elevator, abnormalityLevel, adminTitle, adminContent);
        
        // 2. 向楼栋用户发送通知（只针对严重和中等异常）
        if (isSerious || isModerate) {
            notifyUsersAboutAbnormality(elevator, abnormality, isSerious);
        }
    }

    @Override
    public void handleMaintenanceCompletedNotification(Elevator elevator, int resolvedIssuesCount) {
        // 1. 通知管理员维护完成
        WebSocketMessage adminMessage = WebSocketMessage.builder()
                .type("ELEVATOR_MAINTENANCE")
                .title("电梯维护完成通知")
                .content("楼栋 " + elevator.getBuildingId() + " 的电梯 " + elevator.getElevatorNumber() 
                        + " 维护已完成，已解决 " + resolvedIssuesCount + " 个异常问题。电梯已恢复正常状态。")
                .businessId(elevator.getId())
                .urgent(false)
                .build();
        
        webSocketService.sendMessageToAllAdmins(adminMessage, true);
        log.info("通知管理员电梯维护完成: 电梯ID {}, 楼栋ID {}", 
                elevator.getId(), elevator.getBuildingId());
        
        // 2. 通知楼栋用户电梯恢复使用
        WebSocketMessage userMessage = WebSocketMessage.builder()
                .type("ELEVATOR_MAINTENANCE")
                .title("电梯恢复使用通知")
                .content("您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                        + "维护已完成，现已恢复正常使用。")
                .businessId(elevator.getId())
                .targetId(elevator.getBuildingId())
                .urgent(false)
                .build();
        
        webSocketService.sendMessageToBuildingUsers(elevator.getBuildingId(), userMessage, false);
        log.info("通知楼栋{}用户电梯维护完成", elevator.getBuildingId());
    }

    @Override
    public void notifyPropertyOwners(Elevator elevator, String title, String content, boolean isUrgent) {
        // 获取楼栋中的所有房产
        List<Property> properties = propertyService.getPropertiesByBuildingId(elevator.getBuildingId());
        int notifiedCount = 0;
        
        for (Property property : properties) {
            // 根据房产所有者身份证获取用户
            String ownerIdentity = property.getOwnerIdentity();
            if (ownerIdentity != null && !ownerIdentity.isEmpty()) {
                User user = userService.getByIdCardNumber(ownerIdentity);
                if (user != null) {
                    // 创建通知
                    WebSocketMessage userMessage = WebSocketMessage.builder()
                            .type("ELEVATOR_PROPERTY_NOTICE")
                            .title(title)
                            .content(content + "\n房产信息：楼栋" + property.getBuildingId() + " " + 
                                    property.getUnitNumber() + "单元 " + property.getRoomNumber() + "室")
                            .businessId(elevator.getId())
                            .targetId(user.getId())
                            .urgent(isUrgent)
                            .build();
                    
                    // 发送给用户
                    webSocketService.sendMessageToUser(user.getId(), userMessage, true);
                    notifiedCount++;
                }
            }
        }
        
        log.info("向楼栋 {} 的业主发送电梯通知，成功通知 {} 位业主", elevator.getBuildingId(), notifiedCount);
    }

    @Override
    public void notifyAdminsWithLevel(Elevator elevator, String abnormalityLevel, String title, String content) {
        boolean isSerious = AbnormalityLevelEnum.SERIOUS.getLevel().equals(abnormalityLevel);
        boolean isModerate = AbnormalityLevelEnum.MODERATE.getLevel().equals(abnormalityLevel);
        
        WebSocketMessage adminMessage = WebSocketMessage.builder()
                .type("ELEVATOR_ABNORMALITY")
                .title(title)
                .content(content)
                .businessId(elevator.getId())
                .urgent(isSerious || isModerate)
                .build();
        
        webSocketService.sendMessageToAllAdmins(adminMessage, true);
        log.info("向管理员发送电梯{}级异常通知: 电梯ID {}, 楼栋ID {}", 
                abnormalityLevel, elevator.getId(), elevator.getBuildingId());
    }
    
    /**
     * 通知管理员电梯状态变化
     */
    @Override
    public void notifyAdminsAboutStatus(Elevator elevator, String status) {
        boolean isFault = ElevatorStatusEnum.FAULT.getStatus().equals(status);
        boolean isWarning = ElevatorStatusEnum.WARNING.getStatus().equals(status);
        
        String title;
        String content;
        
        if (isFault) {
            title = "电梯严重异常警报";
            content = "【紧急】楼栋 " + elevator.getBuildingId() + " 的电梯 " + elevator.getElevatorNumber() 
                    + " 出现严重异常，状态：" + status 
                    + "。当前位置：" + elevator.getCurrentFloor() + "层。请立即处理！";
        } else if (isWarning) {
            title = "电梯异常预警";
            content = "【预警】楼栋 " + elevator.getBuildingId() + " 的电梯 " + elevator.getElevatorNumber() 
                    + " 出现异常预警，状态：" + status 
                    + "。当前位置：" + elevator.getCurrentFloor() + "层。需要关注。";
        } else {
            return; // 其他状态无需通知管理员
        }
        
        WebSocketMessage adminMessage = WebSocketMessage.builder()
                .type(isFault ? "ELEVATOR_EMERGENCY" : "ELEVATOR_WARNING")
                .title(title)
                .content(content)
                .businessId(elevator.getId())
                .urgent(true)
                .build();
        
        webSocketService.sendMessageToAllAdmins(adminMessage, true);
        log.info("发送电梯状态变化通知给所有管理员: 电梯ID {}, 楼栋ID {}, 状态: {}", 
                elevator.getId(), elevator.getBuildingId(), status);
    }
    
    /**
     * 通知用户关于电梯异常
     */
    @Override
    public void notifyUsersAboutAbnormality(Elevator elevator, ElevatorAbnormality abnormality, boolean isSerious) {
        // 严重异常通知用户
        String userTitle = isSerious ? "电梯暂停使用通知" : "电梯异常提醒";
        
        // 构建包含异常详情的通知内容
        StringBuilder contentBuilder = new StringBuilder();
        if (isSerious) {
            contentBuilder.append("您所在楼栋").append(elevator.getBuildingId()).append("的电梯").append(elevator.getElevatorNumber())
                    .append("因出现严重异常，现已停止运行。请使用其他电梯或楼梯。");
        } else {
            contentBuilder.append("您所在楼栋").append(elevator.getBuildingId()).append("的电梯").append(elevator.getElevatorNumber())
                    .append("出现异常情况，但仍可使用。建议谨慎乘坐或选择其他电梯。");
        }
        
        // 添加异常详情
        contentBuilder.append("\n异常类型：").append(abnormality.getAbnormalityType());
        if (abnormality.getDescription() != null && !abnormality.getDescription().isEmpty()) {
            contentBuilder.append("\n异常描述：").append(abnormality.getDescription());
        }
        
        String userContent = contentBuilder.toString();
        
        WebSocketMessage userMessage = WebSocketMessage.builder()
                .type("ELEVATOR_ABNORMALITY")
                .title(userTitle)
                .content(userContent)
                .businessId(elevator.getId())
                .targetId(elevator.getBuildingId())
                .urgent(isSerious)
                .build();
        
        webSocketService.sendMessageToBuildingUsers(elevator.getBuildingId(), userMessage, false);
        log.info("向楼栋{}的所有用户发送电梯异常通知: {}", elevator.getBuildingId(), userTitle);
    }
    
    /**
     * 创建异常通知内容
     */
    @Override
    public String createAbnormalityNotificationContent(Elevator elevator, ElevatorAbnormality abnormality, 
                                                       boolean isSerious, boolean isModerate) {
        StringBuilder content = new StringBuilder();
        
        content.append("楼栋 ").append(elevator.getBuildingId())
               .append(" 的电梯 ").append(elevator.getElevatorNumber())
               .append(" 出现").append(abnormality.getAbnormalityLevel()).append("级异常：\n");
        
        // 异常详细信息
        content.append("异常类型：").append(abnormality.getAbnormalityType()).append("\n");
        content.append("异常描述：").append(abnormality.getDescription()).append("\n");
        content.append("异常时间：").append(DateUtil.formatDateTime(abnormality.getOccurrenceTime())).append("\n");
        content.append("电梯当前位置：").append(elevator.getCurrentFloor()).append("层\n");
        
        // 处理建议
        if (isSerious) {
            content.append("请立即安排人员处理！");
        } else if (isModerate) {
            content.append("请尽快安排维护检查。");
        } else {
            content.append("请注意关注电梯状态。");
        }
        
        return content.toString();
    }
} 