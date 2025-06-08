# PropSmart电梯通知系统设计文档

## 概述
电梯通知系统是PropSmart物业管理平台的重要组成部分，负责向不同角色的用户实时推送电梯相关的状态变化、异常情况和维护信息。通知系统采用层级化设计，根据电梯状态和异常的严重程度，分别向不同角色的用户发送针对性通知。

## 系统架构

### 核心组件
1. **ElevatorNotificationService接口**：定义电梯通知的所有功能接口
2. **ElevatorNotificationServiceImpl实现类**：实现通知发送、内容生成等具体逻辑
3. **WebSocketService**：底层消息推送服务，实现消息的实际发送
4. **ElevatorDataSimulatorJob**：电梯数据模拟器，监控电梯运行数据并触发通知

### 数据流程
```
电梯状态变化/异常 → ElevatorNotificationService → WebSocketService → 前端用户接收
```

### 依赖服务
- PropertyService：获取房产信息
- UserService：获取用户信息
- 底层WebSocket服务

## 通知对象类型
系统支持以下几类通知对象：
1. **管理员通知**：物业管理员和维护人员接收所有电梯状态变化和异常情况的通知
2. **楼栋用户通知**：该楼栋所有用户接收电梯运行状态变化的通知
3. **业主专项通知**：根据房产所有者身份证（ownerIdentity）向特定业主发送相关通知

## 通知分级机制
系统根据电梯异常的严重程度将通知分为不同级别：

### 管理员通知分级
1. **严重异常通知**（紧急）
   - 触发条件：电梯出现严重故障（SERIOUS级别异常）
   - 表现形式：高优先级提醒，需要立即处理
   - 通知内容：详细的异常信息、电梯位置、异常类型和描述

2. **中等异常通知**（警告）
   - 触发条件：电梯出现中等级别异常（MODERATE级别异常）
   - 表现形式：中优先级提醒，需要尽快关注
   - 通知内容：异常信息、电梯位置和运行状态

3. **轻微异常通知**（提示）
   - 触发条件：电梯出现轻微异常（MINOR级别异常）
   - 表现形式：低优先级提醒，需要定期检查
   - 通知内容：简要异常信息

4. **维护完成通知**
   - 触发条件：电梯维护工作完成
   - 表现形式：普通通知
   - 通知内容：维护记录、解决的问题数量

### 用户通知分级
1. **故障通知**（紧急）
   - 触发条件：电梯发生严重故障，无法使用
   - 表现形式：高优先级提醒
   - 通知内容：简明的故障提示，建议使用其他电梯或楼梯

2. **异常提醒**（普通）
   - 触发条件：电梯出现异常，但仍可使用
   - 表现形式：普通提醒
   - 通知内容：简短提示，建议谨慎使用

3. **维护通知**（普通）
   - 触发条件：电梯进入维护状态
   - 表现形式：普通提醒
   - 通知内容：维护时间和使用建议

4. **恢复通知**（普通）
   - 触发条件：电梯恢复正常运行
   - 表现形式：普通提醒
   - 通知内容：恢复正常使用的提示

## 通知触发场景
系统会在以下场景自动触发通知：

1. **电梯状态变化**
   - 正常->故障：通知管理员和楼栋用户
   - 正常->维护：通知楼栋用户
   - 故障/维护->正常：通知楼栋用户和管理员

2. **电梯异常检测**
   - 检测到新异常：根据级别通知管理员
   - 严重异常：额外通知楼栋所有用户和业主

3. **电梯维护活动**
   - 维护开始：通知楼栋用户
   - 维护完成：通知楼栋用户和管理员

4. **定期状态更新**
   - 长时间未修复的异常：定期提醒管理员

## 接口设计与功能
ElevatorNotificationService接口提供了一系列方法来处理不同类型的通知：

### 主要处理方法

1. **handleStatusChangeNotification**
   - 处理电梯状态变化通知
   - 参数: `elevator`(电梯信息), `prevStatus`(原状态), `newStatus`(新状态)

2. **handleAbnormalityNotification**
   - 处理电梯异常通知
   - 参数: `elevator`(电梯信息), `abnormality`(异常信息)

3. **handleMaintenanceCompletedNotification**
   - 处理电梯维护完成通知
   - 参数: `elevator`(电梯信息), `resolvedIssuesCount`(解决的问题数量)

### 通知发送方法

4. **notifyPropertyOwners**
   - 向特定房产所有者发送电梯相关通知
   - 参数: `elevator`, `title`, `content`, `isUrgent`

5. **notifyAdminsWithLevel**
   - 向管理员发送异常级别通知
   - 参数: `elevator`, `abnormalityLevel`, `title`, `content`

6. **notifyAdminsAboutStatus**
   - 通知管理员电梯状态变化
   - 参数: `elevator`, `status`

7. **notifyUsersAboutAbnormality**
   - 通知用户关于电梯异常
   - 参数: `elevator`, `abnormality`, `isSerious`

### 内容生成方法

8. **createAbnormalityNotificationContent**
   - 创建异常通知内容
   - 参数: `elevator`, `abnormality`, `isSerious`, `isModerate`
   - 返回: 格式化的通知内容字符串

## 技术实现
系统采用WebSocket实时推送技术，实现以下核心功能：

1. **消息分发**
   - `sendMessageToUser`: 发送消息给特定用户
   - `sendMessageToAllAdmins`: 发送消息给所有管理员
   - `sendMessageToBuildingUsers`: 发送消息给特定楼栋的所有用户

2. **状态监控**
   - 通过ElevatorDataSimulatorJob实时监控电梯状态变化
   - 检测温度、速度、功耗等参数异常

3. **异常级别判定**
   - 根据异常类型和数值判定严重程度
   - 自动分级并触发相应通知

4. **业主定向通知**
   - 根据房产信息及ownerIdentity查找对应业主
   - 发送针对性通知

## 业务流程示例

### 电梯故障流程
1. 系统检测到电梯X出现电机过热严重故障
2. 更新电梯状态为FAULT
3. 创建SERIOUS级别的异常记录
4. 向所有管理员发送紧急通知，包含详细故障信息
5. 向楼栋用户发送故障通知，告知暂停使用
6. 向业主发送特别通知，包含详细房产信息

### 电梯维护流程
1. 管理员对电梯X执行维护操作
2. 更新电梯状态为MAINTENANCE
3. 向楼栋用户发送维护通知
4. 维护完成后，更新电梯状态为NORMAL
5. 向管理员发送维护完成报告，包含已解决问题
6. 向楼栋用户发送恢复使用通知

## 使用指南

### 在服务层集成

```java
@Service
public class ElevatorService {
    
    @Resource
    private ElevatorNotificationService notificationService;
    
    @Transactional
    public void updateElevatorStatus(Long elevatorId, String newStatus) {
        // 1. 获取电梯信息
        Elevator elevator = getById(elevatorId);
        String prevStatus = elevator.getCurrentStatus();
        
        // 2. 更新电梯状态
        elevator.setCurrentStatus(newStatus);
        updateById(elevator);
        
        // 3. 发送通知
        notificationService.handleStatusChangeNotification(elevator, prevStatus, newStatus);
    }
    
    @Transactional
    public void recordElevatorAbnormality(ElevatorAbnormality abnormality) {
        // 1. 保存异常记录
        abnormalityService.save(abnormality);
        
        // 2. 获取电梯信息
        Elevator elevator = getById(abnormality.getElevatorId());
        
        // 3. 发送通知
        notificationService.handleAbnormalityNotification(elevator, abnormality);
    }
}
```

### 针对特定场景的通知

```java
// 示例：通知业主关于电梯维护计划
public void notifyOwnerAboutMaintenance(Elevator elevator, Date maintenanceDate) {
    String title = "电梯计划维护通知";
    String content = String.format("您所在楼栋的电梯计划于%s进行例行维护，维护期间(%s - %s)电梯将暂停使用，请提前做好安排。",
            DateUtil.formatDate(maintenanceDate),
            DateUtil.format(maintenanceDate, "HH:mm"),
            DateUtil.format(DateUtil.offsetHour(maintenanceDate, 2), "HH:mm"));
    
    notificationService.notifyPropertyOwners(elevator, title, content, false);
}
```

## 系统配置
通知系统可根据物业需求进行配置：

- 可自定义哪些类型的中等异常需要通知用户
- 可配置是否启用业主专项通知
- 可设置通知的持久化存储规则

### 配置项示例

```properties
# 通知配置
elevator.notification.persistent.enabled=true            # 是否持久化存储通知
elevator.notification.user.moderate.enabled=true         # 是否将中等异常通知用户
elevator.notification.owner.enabled=true                 # 是否启用业主专项通知
elevator.notification.admin.minor.enabled=true           # 是否将轻微异常通知管理员
```

## 扩展功能

### 短信/App推送集成
系统已预留接口，可方便地扩展为短信通知或App推送：

```java
// 示例：扩展为短信通知
public void sendSmsNotification(User user, String content) {
    // 调用短信服务API
}
```

### 通知历史记录
可扩展实现通知历史记录功能，便于管理和追溯：

```java
// 示例：保存通知记录
public void saveNotificationHistory(WebSocketMessage message) {
    NotificationHistory history = new NotificationHistory();
    history.setType(message.getType());
    history.setTitle(message.getTitle());
    history.setContent(message.getContent());
    history.setTargetId(message.getTargetId());
    history.setBusinessId(message.getBusinessId());
    history.setUrgent(message.isUrgent());
    history.setCreateTime(new Date());
    
    notificationHistoryMapper.insert(history);
}
```

## 最佳实践

1. **异常通知分级处理**：根据异常级别选择合适的通知方式和受众
2. **避免过度通知**：轻微异常可以汇总后通知，避免频繁打扰用户
3. **提供清晰的操作指引**：通知内容应包含明确的后续操作指导
4. **关联业务流程**：通知应与维修计划、处理流程相结合

## 总结
PropSmart电梯通知系统通过分级推送机制，确保不同角色的用户能及时获取与其相关的电梯信息，提高了物业管理的效率和用户体验，同时为电梯安全运行提供了有力保障。系统设计模块化、灵活可扩展，能够适应不同物业的管理需求。 