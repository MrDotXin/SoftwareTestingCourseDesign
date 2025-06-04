package com.mrdotxin.propsmart.service;

import com.mrdotxin.propsmart.model.dto.WebSocketMessage;
import com.mrdotxin.propsmart.model.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知服务
 * 处理不同类型的通知
 */
@Slf4j
@Service
public class NotificationService {

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
            
            WebSocketService.sendMessageToAllAdmins(message);
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
            
            WebSocketService.sendMessageToUser(repairOrder.getUserId().toString(), message);
        }
    }

    /**
     * 处理账单通知
     * @param bill 账单信息
     * @param isNewBill 是否为新账单
     */
    public void handleBillNotification(Bill bill, boolean isNewBill) {
        if (isNewBill) {
            // 新账单，通知相关用户
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("BILL")
                    .title("新账单通知")
                    .content("您有一个新的" + bill.getType() + "账单需要支付，金额：" + bill.getAmount() + "元")
                    .businessId(bill.getId())
                    .targetId(bill.getPropertyId()) // 使用房产ID，实际可能需要查询用户ID
                    .urgent(false)
                    .build();
            
            // 实际应用中，可能需要从房产ID查询到用户ID，然后发送消息
            log.info("发送账单通知给房产{}", bill.getPropertyId());
        } else {
            // 账单状态更新，通知相关用户
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("BILL")
                    .title("账单状态更新")
                    .content("您的账单状态已更新为：" + bill.getStatus())
                    .businessId(bill.getId())
                    .targetId(bill.getPropertyId()) // 使用房产ID，实际可能需要查询用户ID
                    .urgent(false)
                    .build();
            
            // 实际应用中，可能需要从房产ID查询到用户ID，然后发送消息
            log.info("发送账单状态更新通知给房产{}", bill.getPropertyId());
        }
    }

    /**
     * 处理公告通知
     * @param notice 公告信息
     */
    public void handleNoticeNotification(Notice notice) {
        // 新公告，通知所有用户（实际实现可能需要根据公告范围通知不同用户）
        WebSocketMessage message = WebSocketMessage.builder()
                .type("NOTICE")
                .title("新公告")
                .content(notice.getTitle())
                .businessId(notice.getId())
                .urgent(false) // 默认公告非紧急
                .build();
        
        // 这里可以根据公告的范围（如全小区、特定楼栋等）来决定通知哪些用户
        log.info("发布新公告通知：{}", notice.getTitle());
        // 实际应用中需要实现按范围通知的逻辑
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
            
            WebSocketService.sendMessageToAllAdmins(message);
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
            
            WebSocketService.sendMessageToUser(complaint.getUserId().toString(), message);
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
            
            WebSocketService.sendMessageToAllAdmins(message);
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
            
            WebSocketService.sendMessageToUser(visitor.getUserId().toString(), message);
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
            
            WebSocketService.sendMessageToAllAdmins(message);
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
            
            WebSocketService.sendMessageToUser(reservation.getUserId().toString(), message);
        }
    }

    /**
     * 处理电梯状态通知
     * @param elevator 电梯信息
     * @param isAbnormal 是否为异常状态
     */
    public void handleElevatorStatusNotification(Elevator elevator, boolean isAbnormal) {
        if (isAbnormal) {
            // 电梯异常，通知所有管理员
            WebSocketMessage adminMessage = WebSocketMessage.builder()
                    .type("ELEVATOR")
                    .title("电梯异常警报")
                    .content("楼栋 " + elevator.getBuildingId() + " 的电梯 " + elevator.getId() + " 出现异常，状态：" + elevator.getCurrentStatus())
                    .businessId(elevator.getId())
                    .urgent(true)
                    .build();
            
            WebSocketService.sendMessageToAllAdmins(adminMessage);
        }
        
        // 电梯状态变化，通知该楼栋的所有用户
        WebSocketMessage userMessage = WebSocketMessage.builder()
                .type("ELEVATOR")
                .title("电梯状态更新")
                .content("您所在楼栋的电梯状态已更新为：" + elevator.getCurrentStatus())
                .businessId(elevator.getId())
                .targetId(elevator.getBuildingId())
                .urgent(isAbnormal)
                .build();
        
        WebSocketService.sendMessageToBuildingUsers(elevator.getBuildingId(), userMessage);
    }
} 