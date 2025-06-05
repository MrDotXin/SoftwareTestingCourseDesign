package com.mrdotxin.propsmart.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mrdotxin.propsmart.mapper.BuildingMapper;
import com.mrdotxin.propsmart.mapper.ElevatorAbnormalityMapper;
import com.mrdotxin.propsmart.mapper.ElevatorConfigMapper;
import com.mrdotxin.propsmart.mapper.ElevatorMapper;
import com.mrdotxin.propsmart.model.entity.Building;
import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.entity.ElevatorConfig;
import com.mrdotxin.propsmart.model.enums.AbnormalityLevelEnum;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorAbnormalityTypeEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorDirectionEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 电梯数据模拟器任务
 * 负责模拟电梯运行数据并检测异常情况
 */
@Slf4j
@Component
public class ElevatorDataSimulatorJob {

    @Resource
    private ElevatorMapper elevatorMapper;

    @Resource
    private ElevatorConfigMapper elevatorConfigMapper;

    @Resource
    private ElevatorAbnormalityMapper elevatorAbnormalityMapper;

    @Resource
    private BuildingMapper buildingMapper;

    @Resource
    private ElevatorNotificationService elevatorNotificationService;

    private final Random random = new Random();
    
    // 控制模拟器是否运行
    private volatile boolean simulatorRunning = false;
    
    // 存储楼栋的总层数缓存
    private final Map<Long, Integer> buildingTotalLevelsCache = new HashMap<>();
    
    /**
     * 每5秒执行一次数据模拟
     */
    @Scheduled(fixedRate = 5000)
    public void simulateElevatorData() {
        // 只有在模拟器启动状态下才执行
        if (!simulatorRunning) {
            return;
        }
        
        log.info("电梯数据模拟器运行中...");
        
        try {
            // 获取所有电梯数据
            List<Elevator> elevators = elevatorMapper.selectList(null);
            
            for (Elevator elevator : elevators) {
                // 故障或维护中的电梯不进行数据模拟
                if (ElevatorStatusEnum.FAULT.getStatus().equals(elevator.getCurrentStatus()) || 
                    ElevatorStatusEnum.MAINTENANCE.getStatus().equals(elevator.getCurrentStatus())) {
                    continue;
                }
                
                // 获取该电梯的配置
                ElevatorConfig config = elevatorConfigMapper.selectOne(
                        new LambdaQueryWrapper<ElevatorConfig>()
                                .eq(ElevatorConfig::getElevatorId, elevator.getId())
                );
                
                if (config == null) {
                    log.warn("电梯 {} 没有配置信息，跳过模拟", elevator.getId());
                    continue;
                }
                
                // 获取楼栋总层数
                Integer totalLevels = getBuildingTotalLevels(elevator.getBuildingId());
                if (totalLevels == null || totalLevels <= 0) {
                    log.warn("电梯 {} 所属楼栋 {} 总层数无效，使用默认值20", elevator.getId(), elevator.getBuildingId());
                    totalLevels = 20; // 默认值
                }
                
                // 模拟电梯数据
                simulateElevatorMovement(elevator, totalLevels);
                simulateElevatorParameters(elevator);
                
                // 保存更新的电梯数据
                elevator.setUpdateTime(new Date());
                elevatorMapper.updateById(elevator);
                
                // 检查是否有异常
                checkAbnormalities(elevator, config);
            }
        } catch (Exception e) {
            log.error("电梯数据模拟器运行出错: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取楼栋总层数，优先从缓存获取
     */
    private Integer getBuildingTotalLevels(Long buildingId) {
        // 先从缓存获取
        if (buildingTotalLevelsCache.containsKey(buildingId)) {
            return buildingTotalLevelsCache.get(buildingId);
        }
        
        // 缓存中没有则从数据库查询
        Building building = buildingMapper.selectById(buildingId);
        if (building != null && building.getTotalLevels() != null) {
            buildingTotalLevelsCache.put(buildingId, building.getTotalLevels());
            return building.getTotalLevels();
        }
        
        return null;
    }
    
    /**
     * 模拟电梯移动（楼层变化、门状态等）
     */
    private void simulateElevatorMovement(Elevator elevator, int totalLevels) {
        // 当前楼层
        int currentFloor = elevator.getCurrentFloor();
        // 当前运行方向
        String currentDirection = elevator.getRunningDirection();
        
        // 判断当前位置是否在顶层或底层
        boolean isTopFloor = currentFloor >= totalLevels;
        boolean isBottomFloor = currentFloor <= 1;
        
        // 判断电梯当前运行方向
        boolean isMovingUp = ElevatorDirectionEnum.UP.getDirection().equals(currentDirection);
        boolean isMovingDown = ElevatorDirectionEnum.DOWN.getDirection().equals(currentDirection);
        
        // 决定电梯下一步动作的概率
        int directionDecision = random.nextInt(10);
        
        // 根据当前位置决定下一步动作
        if (isTopFloor) {
            // 已到顶层，只能静止或下行
            if (directionDecision < 5) {
                // 50%概率静止
                setElevatorStationary(elevator);
            } else {
                // 50%概率下行
                moveElevatorDown(elevator);
            }
        } else if (isBottomFloor) {
            // 已到底层，只能静止或上行
            if (directionDecision < 5) {
                // 50%概率静止
                setElevatorStationary(elevator);
            } else {
                // 50%概率上行
                moveElevatorUp(elevator, totalLevels);
            }
        } else {
            // 中间楼层，可以任意选择
            if (directionDecision < 3) {
                // 30%概率静止
                setElevatorStationary(elevator);
            } else if (directionDecision < 7) {
                // 40%概率保持当前方向
                if (isMovingUp) {
                    moveElevatorUp(elevator, totalLevels);
                } else if (isMovingDown) {
                    moveElevatorDown(elevator);
                } else {
                    // 当前静止，随机选择方向
                    if (random.nextBoolean()) {
                        moveElevatorUp(elevator, totalLevels);
                    } else {
                        moveElevatorDown(elevator);
                    }
                }
            } else {
                // 30%概率改变方向
                if (isMovingUp) {
                    moveElevatorDown(elevator);
                } else if (isMovingDown) {
                    moveElevatorUp(elevator, totalLevels);
                } else {
                    // 当前静止，随机选择方向
                    if (random.nextBoolean()) {
                        moveElevatorUp(elevator, totalLevels);
                    } else {
                        moveElevatorDown(elevator);
                    }
                }
            }
        }
        
        // 随机设置负载
        elevator.setLoadPercentage(random.nextInt(80)); // 0-80% 负载
    }
    
    /**
     * 设置电梯为静止状态
     */
    private void setElevatorStationary(Elevator elevator) {
        elevator.setRunningDirection(ElevatorDirectionEnum.STATIONARY.getDirection());
        elevator.setRunningSpeed(new BigDecimal("0.0"));
        elevator.setAcceleration(new BigDecimal("0.0"));
        
        // 静止时门有80%概率是开的，20%概率是关的
        if (random.nextDouble() < 0.8) {
            elevator.setDoorStatus("开启");
        } else {
            elevator.setDoorStatus("关闭");
        }
    }
    
    /**
     * 设置电梯上行
     */
    private void moveElevatorUp(Elevator elevator, int totalLevels) {
        elevator.setRunningDirection(ElevatorDirectionEnum.UP.getDirection());
        // 确保不超过总层数
        elevator.setCurrentFloor(Math.min(elevator.getCurrentFloor() + 1, totalLevels));
        elevator.setDoorStatus("关闭");
        
        // 设置速度和加速度
        BigDecimal speed = new BigDecimal(String.valueOf(1.0 + random.nextDouble()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal acceleration = new BigDecimal(String.valueOf(random.nextDouble() * 0.5))
                .setScale(3, RoundingMode.HALF_UP);
                
        elevator.setRunningSpeed(speed);
        elevator.setAcceleration(acceleration);
    }
    
    /**
     * 设置电梯下行
     */
    private void moveElevatorDown(Elevator elevator) {
        elevator.setRunningDirection(ElevatorDirectionEnum.DOWN.getDirection());
        // 确保不小于1层
        elevator.setCurrentFloor(Math.max(elevator.getCurrentFloor() - 1, 1));
        elevator.setDoorStatus("关闭");
        
        // 设置速度和加速度
        BigDecimal speed = new BigDecimal(String.valueOf(1.0 + random.nextDouble()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal acceleration = new BigDecimal(String.valueOf(-random.nextDouble() * 0.5))
                .setScale(3, RoundingMode.HALF_UP);
                
        elevator.setRunningSpeed(speed);
        elevator.setAcceleration(acceleration);
    }
    
    /**
     * 模拟电梯其他参数（温度、功耗等）
     */
    private void simulateElevatorParameters(Elevator elevator) {
        // 模拟轿厢温度 (18-30℃)
        BigDecimal cabinTemp = new BigDecimal(String.valueOf(18 + random.nextDouble() * 12))
                .setScale(1, RoundingMode.HALF_UP);
        elevator.setCabinTemperature(cabinTemp);
        
        // 模拟电机温度 (与是否运动有关)
        BigDecimal baseMotorTemp;
        if (ElevatorDirectionEnum.STATIONARY.getDirection().equals(elevator.getRunningDirection())) {
            // 静止时电机温度较低
            baseMotorTemp = new BigDecimal(String.valueOf(30 + random.nextDouble() * 15));
        } else {
            // 运行时电机温度较高
            baseMotorTemp = new BigDecimal(String.valueOf(40 + random.nextDouble() * 25));
        }
        elevator.setMotorTemperature(baseMotorTemp.setScale(1, RoundingMode.HALF_UP));
        
        // 模拟功耗 (与运动状态和负载有关)
        BigDecimal basePower;
        if (ElevatorDirectionEnum.STATIONARY.getDirection().equals(elevator.getRunningDirection())) {
            // 静止时的待机功耗
            basePower = new BigDecimal("0.5");
        } else {
            // 运行时的基础功耗 + 负载相关功耗
            basePower = new BigDecimal("2.0")
                    .add(new BigDecimal(elevator.getLoadPercentage())
                            .multiply(new BigDecimal("0.03")));
        }
        // 添加一些随机波动
        BigDecimal powerVariation = new BigDecimal(String.valueOf(random.nextDouble() * 0.5 - 0.25));
        BigDecimal powerConsumption = basePower.add(powerVariation).setScale(2, RoundingMode.HALF_UP);
        // 确保功耗不小于0.1
        if (powerConsumption.compareTo(new BigDecimal("0.1")) < 0) {
            powerConsumption = new BigDecimal("0.1");
        }
        elevator.setPowerConsumption(powerConsumption);
    }
    
    /**
     * 检查电梯异常情况
     */
    private void checkAbnormalities(Elevator elevator, ElevatorConfig config) {
        boolean isAbnormal = false;
        String prevStatus = elevator.getCurrentStatus();
        
        // 检查电机温度异常
        if (elevator.getMotorTemperature().compareTo(config.getMaxMotorTemperature()) > 0) {
            // 严重异常
            createAbnormalityRecord(
                elevator, 
                ElevatorAbnormalityTypeEnum.MOTOR_OVERHEATING.getType(), 
                "电机温度超过阈值: " + elevator.getMotorTemperature() + "℃", 
                AbnormalityLevelEnum.SERIOUS.getLevel()
            );
            elevator.setCurrentStatus(ElevatorStatusEnum.FAULT.getStatus());
            isAbnormal = true;
        } 
        // 警告温度
        else if (elevator.getMotorTemperature().compareTo(
                config.getMaxMotorTemperature().multiply(new BigDecimal("0.9"))) > 0) {
            // 中等异常
            createAbnormalityRecord(
                elevator, 
                ElevatorAbnormalityTypeEnum.MOTOR_OVERHEATING.getType(), 
                "电机温度接近阈值: " + elevator.getMotorTemperature() + "℃", 
                AbnormalityLevelEnum.MODERATE.getLevel()
            );
            if (!ElevatorStatusEnum.FAULT.getStatus().equals(elevator.getCurrentStatus())) {
                elevator.setCurrentStatus(ElevatorStatusEnum.WARNING.getStatus());
            }
            isAbnormal = true;
        }
        
        // 检查轿厢温度异常
        if (elevator.getCabinTemperature().compareTo(config.getMaxCabinTemperature()) > 0) {
            // 中等异常
            createAbnormalityRecord(
                elevator, 
                ElevatorAbnormalityTypeEnum.CABIN_OVERHEATING.getType(), 
                "轿厢温度超过阈值: " + elevator.getCabinTemperature() + "℃", 
                AbnormalityLevelEnum.MODERATE.getLevel()
            );
            if (!ElevatorStatusEnum.FAULT.getStatus().equals(elevator.getCurrentStatus())) {
                elevator.setCurrentStatus(ElevatorStatusEnum.WARNING.getStatus());
            }
            isAbnormal = true;
        }
        
        // 检查运行速度异常
        if (elevator.getRunningSpeed().compareTo(config.getMaxSpeed()) > 0) {
            // 严重异常
            createAbnormalityRecord(
                elevator, 
                ElevatorAbnormalityTypeEnum.OVERSPEED.getType(), 
                "电梯速度超过安全阈值: " + elevator.getRunningSpeed() + " m/s", 
                AbnormalityLevelEnum.SERIOUS.getLevel()
            );
            elevator.setCurrentStatus(ElevatorStatusEnum.FAULT.getStatus());
            isAbnormal = true;
        }
        
        // 检查电梯功耗异常
        if (elevator.getPowerConsumption().compareTo(config.getMaxPowerConsumption()) > 0) {
            // 中等异常
            createAbnormalityRecord(
                elevator, 
                ElevatorAbnormalityTypeEnum.POWER_ANOMALY.getType(), 
                "电梯功耗异常: " + elevator.getPowerConsumption() + " kW", 
                AbnormalityLevelEnum.MODERATE.getLevel()
            );
            if (!ElevatorStatusEnum.FAULT.getStatus().equals(elevator.getCurrentStatus())) {
                elevator.setCurrentStatus(ElevatorStatusEnum.WARNING.getStatus());
            }
            isAbnormal = true;
        }
        
        // 随机故障生成 (0.5% 概率)
        if (random.nextDouble() < 0.005 && 
                !ElevatorStatusEnum.FAULT.getStatus().equals(elevator.getCurrentStatus()) &&
                !ElevatorStatusEnum.MAINTENANCE.getStatus().equals(elevator.getCurrentStatus())) {
            // 随机选择故障类型
            ElevatorAbnormalityTypeEnum[] types = ElevatorAbnormalityTypeEnum.values();
            ElevatorAbnormalityTypeEnum randomType = types[random.nextInt(types.length)];
            
            // 80% 概率是中等异常，20% 概率是严重异常
            String abnormalityLevel;
            if (random.nextDouble() < 0.2) {
                abnormalityLevel = AbnormalityLevelEnum.SERIOUS.getLevel();
                elevator.setCurrentStatus(ElevatorStatusEnum.FAULT.getStatus());
            } else {
                abnormalityLevel = AbnormalityLevelEnum.MODERATE.getLevel();
                elevator.setCurrentStatus(ElevatorStatusEnum.WARNING.getStatus());
            }
            
            createAbnormalityRecord(
                elevator, 
                randomType.getType(), 
                "随机模拟的" + randomType.getDescription() + "故障", 
                abnormalityLevel
            );
            
            isAbnormal = true;
        }
        
        // 如果状态改变，更新电梯信息并发送通知
        if (isAbnormal && !prevStatus.equals(elevator.getCurrentStatus())) {
            elevatorMapper.updateById(elevator);
            elevatorNotificationService.handleStatusChangeNotification(elevator, prevStatus, elevator.getCurrentStatus());
            log.info("电梯 {} 状态从 {} 变为 {}", elevator.getId(), prevStatus, elevator.getCurrentStatus());
        }
    }
    
    /**
     * 创建电梯异常记录
     */
    private void createAbnormalityRecord(Elevator elevator, String abnormalityType, 
                                         String description, String abnormalityLevel) {
        // 检查是否已经有相同类型的未解决异常
        LambdaQueryWrapper<ElevatorAbnormality> queryWrapper = new LambdaQueryWrapper<ElevatorAbnormality>()
                .eq(ElevatorAbnormality::getElevatorId, elevator.getId())
                .eq(ElevatorAbnormality::getAbnormalityType, abnormalityType)
                .eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PENDING.getStatus());
                
        Long count = elevatorAbnormalityMapper.selectCount(queryWrapper);
        
        // 如果没有相同类型的未解决异常，则创建新记录
        if (count == 0) {
            ElevatorAbnormality abnormality = new ElevatorAbnormality();
            abnormality.setElevatorId(elevator.getId());
            abnormality.setAbnormalityType(abnormalityType);
            abnormality.setDescription(description);
            abnormality.setAbnormalityLevel(abnormalityLevel);
            abnormality.setStatus(AbnormalityStatusEnum.PENDING.getStatus());
            abnormality.setOccurrenceTime(new Date());
            
            elevatorAbnormalityMapper.insert(abnormality);
            log.info("创建电梯 {} 异常记录: {}", elevator.getId(), description);
            
            // 发送电梯异常通知
            elevatorNotificationService.handleAbnormalityNotification(elevator, abnormality);
            
            // 对于严重异常，通知楼栋业主
            if (AbnormalityLevelEnum.SERIOUS.getLevel().equals(abnormalityLevel)) {
                String title = "电梯严重异常通知";
                String content = String.format(
                    "您所在楼栋%d的电梯%s出现严重异常，现已停止运行。请使用其他电梯或楼梯。\n异常类型：%s\n异常描述：%s",
                    elevator.getBuildingId(), elevator.getElevatorNumber(),
                    abnormalityType, description
                );
                elevatorNotificationService.notifyPropertyOwners(elevator, title, content, true);
            }
        }
    }
    
    /**
     * 启动数据模拟器
     */
    public void startSimulator() {
        this.simulatorRunning = true;
        log.info("电梯数据模拟器已启动");
    }
    
    /**
     * 停止数据模拟器
     */
    public void stopSimulator() {
        this.simulatorRunning = false;
        log.info("电梯数据模拟器已停止");
        // 清空缓存
        this.buildingTotalLevelsCache.clear();
    }
    
    /**
     * 获取模拟器运行状态
     */
    public boolean isRunning() {
        return this.simulatorRunning;
    }
} 