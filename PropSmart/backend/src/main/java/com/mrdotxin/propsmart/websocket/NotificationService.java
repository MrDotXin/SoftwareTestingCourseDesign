package com.mrdotxin.propsmart.websocket;

import com.mrdotxin.propsmart.model.dto.WebSocketMessage;
import com.mrdotxin.propsmart.model.entity.*;
import com.mrdotxin.propsmart.model.enums.AbnormalityLevelEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorStatusEnum;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import cn.hutool.core.date.DateUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * 通知服务
 * 处理不同类型的通知
 */
@Slf4j
@Service
public class NotificationService {

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private PropertyService propertyService;

    @Resource
    private UserService userService;
    /**
     * 处理报修申请通知
     * @param repairOrder 报修单信息
     * @param isNewRequest 是否为新申请
     */
    public void handleRepairOrderNotification(RepairOrder repairOrder, boolean isNewRequest) {
        if (isNewRequest) {
            // 新的报修申请，通知所有管理员
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("REPAIR")
                    .title("新报修申请")
                    .content("收到用户 " + repairOrder.getUserId() + " 的新报修申请，报修内容：" + repairOrder.getDescription())
                    .businessId(repairOrder.getId())
                    .urgent(true) // 假设所有报修申请都是紧急的
                    .build();
            
            webSocketService.sendMessageToAllAdmins(message, false);
        } else {
            // 报修状态更新，通知相关用户
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("REPAIR")
                    .title("报修状态更新")
                    .content("您的报修申请状态已更新为：" + repairOrder.getStatus())
                    .businessId(repairOrder.getId())
                    .targetId(repairOrder.getUserId())
                    .urgent(false)
                    .build();
            
            webSocketService.sendMessageToUser(repairOrder.getUserId(), message, false);
        }
    }

    /**
     * 处理账单通知
     * @param bill 账单信息
     * @param isNewBill 是否为新账单
     */
    public void  handleBillNotification(Bill bill, boolean isNewBill) {
        WebSocketMessage message = null;
        if (isNewBill) {
            message = WebSocketMessage.builder()
                    .type("BILL")
                    .title("新账单通知")
                    .content("您有一个新的" + bill.getType() + "账单需要支付，金额：" + bill.getAmount() + "元")
                    .businessId(bill.getId())
                    .targetId(bill.getPropertyId()) // 使用房产ID，实际可能需要查询用户ID
                    .urgent(false)
                    .build();
        } else {
            message = WebSocketMessage.builder()
                    .type("BILL")
                    .title("账单状态更新")
                    .content("您的账单状态已更新为：" + bill.getStatus())
                    .businessId(bill.getId())
                    .targetId(bill.getPropertyId())
                    .urgent(false)
                    .build();
        }

        Property property = propertyService.getById(bill.getPropertyId());
        User user = userService.getByIdCardNumber(property.getOwnerIdentity());
        Long userId = user.getId();

        webSocketService.sendMessageToUser(userId, message, true);
    }

    /**
     * 处理账单通知
     */
    public void handleAbnormalEnergyConsumptionNotification(User user, Property property, String msg) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type("EnergyConsumption")
                .title("能源异常提醒更新")
                .content(msg)
                .businessId(user.getId())
                .targetId(property.getId())
                .urgent(true)
                .build();

        Long userId = user.getId();

        webSocketService.sendMessageToUser(userId, message, true);
    }

    /**
     * 处理账单通知
     */
    public void handleAbnormalBillNotification(User user, Property property, String msg) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type("Bill")
                .title("账单异常提醒更新")
                .content(msg)
                .businessId(user.getId())
                .targetId(property.getId())
                .urgent(true)
                .build();

        Long userId = user.getId();

        webSocketService.sendMessageToUser(userId, message, true);
    }

    /**
     * 处理公告通知
     * @param notice 公告信息
     */
    public void handleNoticeNotification(Notice notice) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type("NOTICE")
                .title("新公告")
                .content(notice.getTitle())
                .businessId(notice.getId())
                .urgent(false) // 默认公告非紧急
                .build();

        log.info("发布新公告通知：{}", notice.getTitle());

        webSocketService.sendMessageToAll(message, false);
    }

    /**
     * 处理投诉建议通知
     * @param complaint 投诉建议信息
     * @param isNew 是否为新投诉建议
     */
    public void handleComplaintNotification(ComplaintSuggestion complaint, boolean isNew) {
        if (isNew) {
            // 新的投诉建议，通知所有管理员
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("COMPLAINT")
                    .title("新" + (complaint.getType().equals("complaint") ? "投诉" : "建议"))
                    .content("收到用户 " + complaint.getUserId() + " 的" + (complaint.getType().equals("complaint") ? "投诉" : "建议") + 
                             "，内容：" + complaint.getContent())
                    .businessId(complaint.getId())
                    .urgent(complaint.getType().equals("complaint"))  // 投诉为紧急，建议为非紧急
                    .build();
            
            webSocketService.sendMessageToAllAdmins(message, false);
        } else {
            // 投诉建议状态更新，通知相关用户
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("COMPLAINT")
                    .title((complaint.getType().equals("complaint") ? "投诉" : "建议") + "处理更新")
                    .content("您的" + (complaint.getType().equals("complaint") ? "投诉" : "建议") + 
                             "已处理，状态更新为：" + complaint.getStatus())
                    .businessId(complaint.getId())
                    .targetId(complaint.getUserId())
                    .urgent(false)
                    .build();
            
            webSocketService.sendMessageToUser(complaint.getUserId(), message, false);
        }
    }

    /**
     * 处理访客通知
     * @param visitor 访客信息
     * @param isNew 是否为新访客申请
     */
    public void handleVisitorNotification(Visitor visitor, boolean isNew) {
        if (isNew) {
            // 新的访客申请，通知所有管理员
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("VISITOR")
                    .title("新访客申请")
                    .content("收到访客 " + visitor.getVisitorName() + " 的访问申请，访问对象用户ID：" + visitor.getUserId())
                    .businessId(visitor.getId())
                    .urgent(false)
                    .build();
            
            webSocketService.sendMessageToAllAdmins(message, false);
        } else {
            // 访客申请状态更新，通知相关用户
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("VISITOR")
                    .title("访客申请状态更新")
                    .content("您的访客 " + visitor.getVisitorName() + " 的申请状态已更新为：" + visitor.getReviewStatus())
                    .businessId(visitor.getId())
                    .targetId(visitor.getUserId())
                    .urgent(false)
                    .build();
            
            webSocketService.sendMessageToUser(visitor.getUserId(), message, false);
        }
    }

    /**
     * 处理设施预订通知
     * @param reservation 设施预订信息
     * @param isNew 是否为新预订
     */
    public void handleFacilityReservationNotification(FacilityReservation reservation, boolean isNew) {
        if (isNew) {
            // 新的设施预订，通知所有管理员
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("FACILITY")
                    .title("新设施预订")
                    .content("收到用户 " + reservation.getUserId() + " 的设施预订申请，设施ID：" + reservation.getFacilityId())
                    .businessId(reservation.getId())
                    .urgent(false)
                    .build();
            
            webSocketService.sendMessageToAllAdmins(message, false);
        } else {
            // 设施预订状态更新，通知相关用户
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("FACILITY")
                    .title("设施预订状态更新")
                    .content("您的设施预订申请状态已更新为：" + reservation.getStatus())
                    .businessId(reservation.getId())
                    .targetId(reservation.getUserId())
                    .urgent(false)
                    .build();
            
            webSocketService.sendMessageToUser(reservation.getUserId(), message, false);
        }
    }

    /**
     * 处理电梯状态通知
     * @param elevator 电梯信息
     * @param isAbnormal 是否为异常状态
     */
    public void handleElevatorStatusNotification(Elevator elevator, boolean isAbnormal) {
        // 根据电梯状态决定通知内容和紧急程度
        String elevatorStatus = elevator.getCurrentStatus();
        boolean isFault = ElevatorStatusEnum.FAULT.getStatus().equals(elevatorStatus);
        boolean isWarning = ElevatorStatusEnum.WARNING.getStatus().equals(elevatorStatus);
        boolean isMaintenance = ElevatorStatusEnum.MAINTENANCE.getStatus().equals(elevatorStatus);
        
        // 1. 处理管理员通知 - 根据异常级别发送不同通知
        if (isAbnormal) {
            // 严重异常 - 高优先级通知
            if (isFault) {
                WebSocketMessage adminEmergencyMessage = WebSocketMessage.builder()
                        .type("ELEVATOR_EMERGENCY")
                        .title("电梯严重异常警报")
                        .content("【紧急】楼栋 " + elevator.getBuildingId() + " 的电梯 " + elevator.getElevatorNumber() 
                                + " 出现严重异常，状态：" + elevatorStatus 
                                + "。当前位置：" + elevator.getCurrentFloor() + "层。请立即处理！")
                        .businessId(elevator.getId())
                        .urgent(true)
                        .build();
                
                webSocketService.sendMessageToAllAdmins(adminEmergencyMessage, true);
                log.info("发送电梯严重异常紧急通知给所有管理员: 电梯ID {}, 楼栋ID {}", 
                        elevator.getId(), elevator.getBuildingId());
            } 
            // 预警异常 - 中优先级通知
            else if (isWarning) {
                WebSocketMessage adminWarningMessage = WebSocketMessage.builder()
                        .type("ELEVATOR_WARNING")
                        .title("电梯异常预警")
                        .content("【预警】楼栋 " + elevator.getBuildingId() + " 的电梯 " + elevator.getElevatorNumber() 
                                + " 出现异常预警，状态：" + elevatorStatus 
                                + "。当前位置：" + elevator.getCurrentFloor() + "层。需要关注。")
                        .businessId(elevator.getId())
                        .urgent(true)
                        .build();
                
                webSocketService.sendMessageToAllAdmins(adminWarningMessage, true);
                log.info("发送电梯异常预警通知给所有管理员: 电梯ID {}, 楼栋ID {}", 
                        elevator.getId(), elevator.getBuildingId());
            }
        }
        
        // 2. 处理用户通知 - 只在状态变化时通知该楼栋的用户
        String userTitle;
        String userContent;
        boolean userUrgent = false;
        
        if (isFault) {
            userTitle = "电梯故障通知";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "出现故障，当前无法使用。请选择其他电梯或楼梯。维修人员正在处理中。";
            userUrgent = true;
        } else if (isWarning) {
            userTitle = "电梯异常提醒";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "出现异常情况，但仍可使用。建议谨慎乘坐。";
            userUrgent = false;
        } else if (isMaintenance) {
            userTitle = "电梯维护通知";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "正在进行维护，暂时无法使用。请使用其他电梯或楼梯。";
            userUrgent = false;
        } else {
            userTitle = "电梯恢复正常";
            userContent = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                    + "已恢复正常运行，可以正常使用。";
            userUrgent = false;
        }
        
        // 创建用户通知消息
        WebSocketMessage userMessage = WebSocketMessage.builder()
                .type("ELEVATOR_STATUS")
                .title(userTitle)
                .content(userContent)
                .businessId(elevator.getId())
                .targetId(elevator.getBuildingId())
                .urgent(userUrgent)
                .build();
        
        // 发送通知给该楼栋的用户
        webSocketService.sendMessageToBuildingUsers(elevator.getBuildingId(), userMessage, false);
        log.info("向楼栋{}的所有用户发送电梯状态更新: {}", elevator.getBuildingId(), userTitle);
    }
    
    /**
     * 处理电梯特定异常通知
     * @param elevator 电梯信息
     * @param abnormality 电梯异常信息
     */
    public void handleElevatorAbnormalityNotification(Elevator elevator, ElevatorAbnormality abnormality) {
        // 获取异常级别
        String abnormalityLevel = abnormality.getAbnormalityLevel();
        boolean isSerious = AbnormalityLevelEnum.SERIOUS.getLevel().equals(abnormalityLevel);
        boolean isModerate = AbnormalityLevelEnum.MODERATE.getLevel().equals(abnormalityLevel);
        
        // 1. 向管理员发送异常通知
        String adminTitle = isSerious ? "电梯严重异常报警" : (isModerate ? "电梯中等异常通知" : "电梯轻微异常提醒");
        
        WebSocketMessage adminMessage = WebSocketMessage.builder()
                .type("ELEVATOR_ABNORMALITY")
                .title(adminTitle)
                .content("楼栋 " + elevator.getBuildingId() + " 的电梯 " + elevator.getElevatorNumber() 
                        + " 出现" + abnormalityLevel + "级异常：\n" 
                        + "异常类型：" + abnormality.getAbnormalityType() + "\n"
                        + "异常描述：" + abnormality.getDescription() + "\n"
                        + "异常时间：" + DateUtil.formatDateTime(abnormality.getOccurrenceTime()) + "\n"
                        + (isSerious ? "请立即安排人员处理！" : (isModerate ? "请尽快处理" : "请注意关注")))
                .businessId(elevator.getId())
                .urgent(isSerious || isModerate)
                .build();
        
        webSocketService.sendMessageToAllAdmins(adminMessage, true);
        
        // 2. 向楼栋用户发送通知（只针对严重和中等异常）
        if (isSerious || isModerate) {
            // 严重异常通知用户
            if (isSerious) {
                WebSocketMessage userMessage = WebSocketMessage.builder()
                        .type("ELEVATOR_ABNORMALITY")
                        .title("电梯暂停使用通知")
                        .content("您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                                + "因出现严重异常，现已停止运行。请使用其他电梯或楼梯。")
                        .businessId(elevator.getId())
                        .targetId(elevator.getBuildingId())
                        .urgent(true)
                        .build();
                
                webSocketService.sendMessageToBuildingUsers(elevator.getBuildingId(), userMessage, false);
            }
            // 中等异常可选择性通知用户
            else if (isModerate && shouldNotifyUsersForModerateAbnormality(abnormality.getAbnormalityType())) {
                WebSocketMessage userMessage = WebSocketMessage.builder()
                        .type("ELEVATOR_ABNORMALITY")
                        .title("电梯异常提醒")
                        .content("您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                                + "出现异常情况，但仍可使用。建议谨慎乘坐或选择其他电梯。")
                        .businessId(elevator.getId())
                        .targetId(elevator.getBuildingId())
                        .urgent(false)
                        .build();
                
                webSocketService.sendMessageToBuildingUsers(elevator.getBuildingId(), userMessage, false);
            }
        }
    }
    
    /**
     * 电梯维护完成通知
     * @param elevator 电梯信息
     * @param resolvedAbnormalitiesCount 已解决的异常数量
     */
    public void handleElevatorMaintenanceCompletedNotification(Elevator elevator, int resolvedAbnormalitiesCount) {
        // 1. 通知管理员维护完成
        WebSocketMessage adminMessage = WebSocketMessage.builder()
                .type("ELEVATOR_MAINTENANCE")
                .title("电梯维护完成通知")
                .content("楼栋 " + elevator.getBuildingId() + " 的电梯 " + elevator.getElevatorNumber() 
                        + " 维护已完成，已解决 " + resolvedAbnormalitiesCount + " 个异常问题。电梯已恢复正常状态。")
                .businessId(elevator.getId())
                .urgent(false)
                .build();
        
        webSocketService.sendMessageToAllAdmins(adminMessage, true);
        
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
    }
    
    /**
     * 决定是否应该为中等异常通知用户
     * 某些类型的中等异常可能不需要通知普通用户，避免过多打扰
     */
    private boolean shouldNotifyUsersForModerateAbnormality(String abnormalityType) {
        // 根据业务需求定义哪些中等异常需要通知用户
        // 例如，可能只有影响用户体验的异常才通知
        return true; // 默认都通知，可根据业务需求修改
    }
    
    /**
     * 向特定房产的所有者发送电梯通知
     * @param elevator 电梯信息
     * @param message 消息内容
     */
    public void sendElevatorNotificationToPropertyOwners(Elevator elevator, String message) {
        // 获取楼栋中的所有房产
        List<Property> properties = propertyService.getPropertiesByBuildingId(elevator.getBuildingId());
        
        for (Property property : properties) {
            // 根据房产所有者身份证获取用户
            String ownerIdentity = property.getOwnerIdentity();
            if (ownerIdentity != null && !ownerIdentity.isEmpty()) {
                User user = userService.getByIdCardNumber(ownerIdentity);
                if (user != null) {
                    // 创建通知
                    WebSocketMessage userMessage = WebSocketMessage.builder()
                            .type("ELEVATOR_PROPERTY_NOTICE")
                            .title("您所在房产相关电梯通知")
                            .content(message)
                            .businessId(elevator.getId())
                            .targetId(user.getId())
                            .urgent(false)
                            .build();
                    
                    // 发送给用户
                    webSocketService.sendMessageToUser(user.getId(), userMessage, false);
                }
            }
        }
    }
} 