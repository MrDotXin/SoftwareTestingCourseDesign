package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.mapper.ElevatorMapper;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorBasicInfoDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorConfigDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorDetailDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorAbnormalityDTO;
import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.entity.ElevatorConfig;
import com.mrdotxin.propsmart.model.enums.AbnormalityLevelEnum;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorAbnormalityTypeEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorDirectionEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorAbnormalityService;
import com.mrdotxin.propsmart.service.ElevatorConfigService;
import com.mrdotxin.propsmart.service.ElevatorService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

/**
 * 电梯服务实现类
 */
@Service
public class ElevatorServiceImpl extends ServiceImpl<ElevatorMapper, Elevator> implements ElevatorService {

    @Resource
    private ElevatorConfigService elevatorConfigService;
    
    @Resource
    private ElevatorAbnormalityService elevatorAbnormalityService;
    
    // 电梯数据模拟器
    private ScheduledExecutorService simulator;
    
    // 电梯运行状态缓存，避免频繁数据库访问
    private final ConcurrentHashMap<Long, Elevator> elevatorCache = new ConcurrentHashMap<>();
    
    // 随机数生成器
    private final Random random = new Random();

    @Override
    public List<ElevatorBasicInfoDTO> listElevatorBasicInfo() {
        List<Elevator> elevators = list();
        return elevators.stream().map(this::convertToBasicInfo).collect(Collectors.toList());
    }

    @Override
    public ElevatorBasicInfoDTO getElevatorBasicInfo(Long elevatorId) {
        Elevator elevator = getById(elevatorId);
        if (elevator == null) {
            return null;
        }
        return convertToBasicInfo(elevator);
    }

    @Override
    public List<ElevatorDetailDTO> listElevatorDetails() {
        List<Elevator> elevators = list();
        return elevators.stream().map(this::convertToDetailInfo).collect(Collectors.toList());
    }

    @Override
    public ElevatorDetailDTO getElevatorDetail(Long elevatorId) {
        Elevator elevator = getById(elevatorId);
        if (elevator == null) {
            return null;
        }
        return convertToDetailInfo(elevator);
    }

    @Override
    public boolean updateMaintenanceDate(Long elevatorId) {
        Elevator elevator = getById(elevatorId);
        if (elevator == null) {
            return false;
        }
        
        Date now = new Date();
        elevator.setLastMaintenanceDate(now);
        elevator.setCurrentStatus(ElevatorStatusEnum.NORMAL.getStatus());
        boolean result = updateById(elevator);
        
        // 更新缓存
        if (result && elevatorCache.containsKey(elevatorId)) {
            elevatorCache.put(elevatorId, elevator);
            
            // 处理相关未关闭的异常
            resolveOutstandingAbnormalities(elevatorId, now);
        }
        
        return result;
    }

    @Override
    public boolean updateElevatorStatus(Long elevatorId, String status) {
        Elevator elevator = getById(elevatorId);
        if (elevator == null) {
            return false;
        }
        
        elevator.setCurrentStatus(status);
        boolean result = updateById(elevator);
        
        // 更新缓存
        if (result && elevatorCache.containsKey(elevatorId)) {
            elevatorCache.put(elevatorId, elevator);
        }
        
        return result;
    }

    @Override
    @PostConstruct
    public void startElevatorDataSimulator() {
        if (simulator != null && !simulator.isShutdown()) {
            return;
        }
        
        // 初始化电梯缓存
        List<Elevator> elevators = list();
        for (Elevator elevator : elevators) {
            elevatorCache.put(elevator.getId(), elevator);
            
            // 为每个电梯创建默认配置（如果不存在）
            elevatorConfigService.createDefaultConfig(elevator.getId());
        }
        
        // 创建定时任务，模拟电梯实时数据
        simulator = Executors.newScheduledThreadPool(1);
        simulator.scheduleAtFixedRate(this::simulateElevatorData, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    @PreDestroy
    public void stopElevatorDataSimulator() {
        if (simulator != null && !simulator.isShutdown()) {
            simulator.shutdown();
            try {
                if (!simulator.awaitTermination(5, TimeUnit.SECONDS)) {
                    simulator.shutdownNow();
                }
            } catch (InterruptedException e) {
                simulator.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 模拟电梯实时数据
     */
    private void simulateElevatorData() {
        try {
            List<Elevator> elevatorList = new ArrayList<>(elevatorCache.values());
            List<Elevator> updatedElevators = new ArrayList<>();
            
            for (Elevator elevator : elevatorList) {
                // 检查电梯状态是否允许数据模拟
                if (!isElevatorSimulationAllowed(elevator)) {
                    // 仅更新时间戳，不更新其他数据
                    elevator.setUpdateTime(new Date());
                    updatedElevators.add(elevator);
                    continue;
                }
                
                // 获取电梯配置
                ElevatorConfigDTO config = elevatorConfigService.getElevatorConfig(elevator.getId());
                if (config == null) {
                    continue;
                }
                
                // 模拟电梯运行数据
                simulateElevatorRunningData(elevator);
                
                // 检查是否有异常
                checkAbnormalities(elevator, config);
                
                updatedElevators.add(elevator);
            }
            
            // 批量更新电梯数据
            if (!updatedElevators.isEmpty()) {
                updateBatchById(updatedElevators);
            }
        } catch (Exception e) {
            // 记录异常但不中断模拟器运行
            e.printStackTrace();
        }
    }
    
    /**
     * 检查电梯状态是否允许数据模拟
     * @param elevator 电梯对象
     * @return 是否允许模拟
     */
    private boolean isElevatorSimulationAllowed(Elevator elevator) {
        String status = elevator.getCurrentStatus();
        
        // 故障和维护中的电梯不进行数据模拟
        if (status.equals(ElevatorStatusEnum.FAULT.getStatus()) || 
                status.equals(ElevatorStatusEnum.MAINTENANCE.getStatus())) {
            // 查询异常记录，检查是否需要持续维护
            List<ElevatorAbnormalityDTO> abnormalities = elevatorAbnormalityService
                    .listAbnormalitiesByElevatorId(elevator.getId());
            
            // 如果最近有未解决的严重异常，则电梯仍然需要维护
            boolean hasUnresolvedSevereAbnormality = abnormalities.stream()
                    .anyMatch(a -> 
                        AbnormalityLevelEnum.SEVERE.getLevel().equals(a.getAbnormalityLevel()) && 
                        (AbnormalityStatusEnum.PENDING.getStatus().equals(a.getStatus()) ||
                        AbnormalityStatusEnum.PROCESSING.getStatus().equals(a.getStatus()))
                    );
            
            // 如果有未解决的严重异常，则不允许模拟数据
            return !hasUnresolvedSevereAbnormality;
        }
        
        return true;
    }
    
    /**
     * 模拟电梯运行数据
     */
    private void simulateElevatorRunningData(Elevator elevator) {
        // 模拟电梯楼层变化
        simulateFloorChange(elevator);
        
        // 模拟负载变化 (0-100%)
        elevator.setLoadPercentage(random.nextInt(80) + 1); // 通常不会满载
        
        // 模拟门状态
        if (elevator.getRunningDirection().equals(ElevatorDirectionEnum.STATIONARY.getDirection())) {
            // 静止状态有50%概率门是开着的
            elevator.setDoorStatus(random.nextBoolean() ? "开启" : "关闭");
        } else {
            // 运行时门必须关闭
            elevator.setDoorStatus("关闭");
        }
        
        // 模拟温度变化
        // 轿厢温度通常在18-30度
        elevator.setCabinTemperature(BigDecimal.valueOf(18 + random.nextDouble() * 12).setScale(2, RoundingMode.HALF_UP));
        // 电机温度通常在30-70度
        elevator.setMotorTemperature(BigDecimal.valueOf(30 + random.nextDouble() * 40).setScale(2, RoundingMode.HALF_UP));
        
        // 模拟速度变化
        if (!elevator.getRunningDirection().equals(ElevatorDirectionEnum.STATIONARY.getDirection())) {
            // 运行速度在额定速度的80%-110%之间波动
            double ratedSpeed = elevator.getRatedSpeed().doubleValue();
            double speedFactor = 0.8 + random.nextDouble() * 0.3; // 0.8-1.1
            elevator.setRunningSpeed(BigDecimal.valueOf(ratedSpeed * speedFactor).setScale(2, RoundingMode.HALF_UP));
            
            // 模拟加速度，通常在0.1-1.2 m/s²
            elevator.setAcceleration(BigDecimal.valueOf(0.1 + random.nextDouble() * 1.1).setScale(3, RoundingMode.HALF_UP));
        } else {
            // 静止状态
            elevator.setRunningSpeed(BigDecimal.ZERO);
            elevator.setAcceleration(BigDecimal.ZERO);
        }
        
        // 模拟功耗
        // 静止状态功耗较低，运行状态功耗较高
        if (elevator.getRunningDirection().equals(ElevatorDirectionEnum.STATIONARY.getDirection())) {
            elevator.setPowerConsumption(BigDecimal.valueOf(0.5 + random.nextDouble() * 1.5).setScale(2, RoundingMode.HALF_UP));
        } else {
            // 运行功耗与负载和速度相关
            double loadFactor = elevator.getLoadPercentage() / 100.0;
            double speedFactor = elevator.getRunningSpeed().doubleValue() / elevator.getRatedSpeed().doubleValue();
            double power = 2.0 + 8.0 * loadFactor * speedFactor;
            elevator.setPowerConsumption(BigDecimal.valueOf(power).setScale(2, RoundingMode.HALF_UP));
        }
        
        // 更新时间
        elevator.setUpdateTime(new Date());
    }
    
    /**
     * 模拟电梯楼层变化
     */
    private void simulateFloorChange(Elevator elevator) {
        // 电梯方向变化逻辑
        if (elevator.getRunningDirection().equals(ElevatorDirectionEnum.STATIONARY.getDirection())) {
            // 静止状态有30%概率开始运动
            if (random.nextDouble() < 0.3) {
                // 随机选择上行或下行
                elevator.setRunningDirection(random.nextBoolean() ? 
                        ElevatorDirectionEnum.UP.getDirection() : 
                        ElevatorDirectionEnum.DOWN.getDirection());
            }
        } else {
            // 运动状态有20%概率停止
            if (random.nextDouble() < 0.2) {
                elevator.setRunningDirection(ElevatorDirectionEnum.STATIONARY.getDirection());
                return;
            }
            
            // 根据方向更新楼层
            int currentFloor = elevator.getCurrentFloor();
            if (elevator.getRunningDirection().equals(ElevatorDirectionEnum.UP.getDirection())) {
                // 上行
                if (currentFloor < 20) { // 假设最高20层
                    elevator.setCurrentFloor(currentFloor + 1);
                } else {
                    // 到顶层后改为下行
                    elevator.setRunningDirection(ElevatorDirectionEnum.DOWN.getDirection());
                }
            } else {
                // 下行
                if (currentFloor > 1) {
                    elevator.setCurrentFloor(currentFloor - 1);
                } else {
                    // 到底层后改为上行
                    elevator.setRunningDirection(ElevatorDirectionEnum.UP.getDirection());
                }
            }
        }
    }
    
    /**
     * 检查电梯异常情况
     */
    private void checkAbnormalities(Elevator elevator, ElevatorConfigDTO config) {
        boolean hasAbnormality = false;
        boolean isSevereAbnormality = false;
        ElevatorAbnormality abnormality = null;
        
        // 检查轿厢温度
        if (elevator.getCabinTemperature().compareTo(config.getCabinTempAlertThr()) > 0) {
            abnormality = createAbnormality(elevator, ElevatorAbnormalityTypeEnum.TEMPERATURE_ABNORMAL.getType(), 
                    "轿厢温度超过预警阈值：" + elevator.getCabinTemperature() + "℃");
            hasAbnormality = true;
        }
        
        // 检查电机温度
        if (elevator.getMotorTemperature().compareTo(config.getMotorTempAlertThr()) > 0) {
            abnormality = createAbnormality(elevator, ElevatorAbnormalityTypeEnum.TEMPERATURE_ABNORMAL.getType(), 
                    "电机温度超过预警阈值：" + elevator.getMotorTemperature() + "℃");
            hasAbnormality = true;
        }
        
        // 检查速度异常
        if (!elevator.getRunningDirection().equals(ElevatorDirectionEnum.STATIONARY.getDirection())) {
            BigDecimal ratedSpeed = elevator.getRatedSpeed();
            BigDecimal actualSpeed = elevator.getRunningSpeed();
            BigDecimal allowedDeviation = ratedSpeed.multiply(config.getSpeedAlertPercent().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            
            if (actualSpeed.subtract(ratedSpeed).abs().compareTo(allowedDeviation) > 0) {
                abnormality = createAbnormality(elevator, ElevatorAbnormalityTypeEnum.SPEED_ABNORMAL.getType(), 
                        "运行速度异常：" + actualSpeed + " m/s，额定速度：" + ratedSpeed + " m/s");
                hasAbnormality = true;
            }
        }
        
        // 检查加速度异常
        if (elevator.getAcceleration().compareTo(config.getAccelAlertThr()) > 0) {
            abnormality = createAbnormality(elevator, ElevatorAbnormalityTypeEnum.ACCELERATION_ABNORMAL.getType(), 
                    "加速度异常：" + elevator.getAcceleration() + " m/s²，阈值：" + config.getAccelAlertThr() + " m/s²");
            hasAbnormality = true;
        }
        
        // 检查超载情况
        if (elevator.getLoadPercentage() > 90) {
            abnormality = createAbnormality(elevator, ElevatorAbnormalityTypeEnum.OVERLOAD.getType(), 
                    "电梯超载：" + elevator.getLoadPercentage() + "%");
            hasAbnormality = true;
        }
        
        // 随机故障模拟（低概率）
        if (!hasAbnormality && random.nextDouble() < 0.01) { // 1%概率发生随机故障
            String[] faultTypes = {
                ElevatorAbnormalityTypeEnum.DOOR_FAULT.getType(),
                ElevatorAbnormalityTypeEnum.SENSOR_ABNORMAL.getType(),
                ElevatorAbnormalityTypeEnum.POWER_OUTAGE.getType()
            };
            String faultType = faultTypes[random.nextInt(faultTypes.length)];
            abnormality = createAbnormality(elevator, faultType, "随机模拟故障：" + faultType);
            hasAbnormality = true;
            
            // 门故障和停电是严重故障
            if (faultType.equals(ElevatorAbnormalityTypeEnum.DOOR_FAULT.getType()) || 
                    faultType.equals(ElevatorAbnormalityTypeEnum.POWER_OUTAGE.getType())) {
                isSevereAbnormality = true;
            }
        }
        
        // 更新电梯状态
        if (hasAbnormality) {
            // 根据异常级别决定电梯状态
            if (abnormality.getAbnormalityLevel().equals(AbnormalityLevelEnum.SEVERE.getLevel()) || isSevereAbnormality) {
                elevator.setCurrentStatus(ElevatorStatusEnum.FAULT.getStatus());
                
                // 对于严重故障，需停止电梯运行
                if (elevator.getRunningDirection() != null && 
                    !elevator.getRunningDirection().equals(ElevatorDirectionEnum.STATIONARY.getDirection())) {
                    elevator.setRunningDirection(ElevatorDirectionEnum.STATIONARY.getDirection());
                    elevator.setRunningSpeed(BigDecimal.ZERO);
                    elevator.setAcceleration(BigDecimal.ZERO);
                    elevator.setPowerConsumption(BigDecimal.valueOf(0.5)); // 最小待机功耗
                    
                    // 更新门状态 - 门故障时可能是开着的
                    if (abnormality.getAbnormalityType().equals(ElevatorAbnormalityTypeEnum.DOOR_FAULT.getType())) {
                        elevator.setDoorStatus("半开"); // 模拟门卡住的状态
                    }
                }
            } else {
                elevator.setCurrentStatus(ElevatorStatusEnum.WARNING.getStatus());
            }
            
            // 记录异常
            Long abnormalityId = elevatorAbnormalityService.createAbnormality(abnormality);
            
            // 日志记录
            System.out.println("电梯ID: " + elevator.getId() + " 发生异常, 异常ID: " + abnormalityId + 
                    ", 异常类型: " + abnormality.getAbnormalityType() + 
                    ", 异常级别: " + abnormality.getAbnormalityLevel());
        }
    }
    
    /**
     * 创建电梯异常记录
     */
    private ElevatorAbnormality createAbnormality(Elevator elevator, String type, String description) {
        ElevatorAbnormality abnormality = new ElevatorAbnormality();
        abnormality.setElevatorId(elevator.getId());
        abnormality.setAbnormalityType(type);
        
        // 根据异常类型设置级别
        if (type.equals(ElevatorAbnormalityTypeEnum.POWER_OUTAGE.getType()) || 
                type.equals(ElevatorAbnormalityTypeEnum.DOOR_FAULT.getType())) {
            abnormality.setAbnormalityLevel(AbnormalityLevelEnum.SEVERE.getLevel());
        } else if (type.equals(ElevatorAbnormalityTypeEnum.TEMPERATURE_ABNORMAL.getType()) || 
                type.equals(ElevatorAbnormalityTypeEnum.SPEED_ABNORMAL.getType()) || 
                type.equals(ElevatorAbnormalityTypeEnum.ACCELERATION_ABNORMAL.getType())) {
            abnormality.setAbnormalityLevel(AbnormalityLevelEnum.MODERATE.getLevel());
        } else {
            abnormality.setAbnormalityLevel(AbnormalityLevelEnum.MINOR.getLevel());
        }
        
        abnormality.setOccurrenceTime(new Date());
        abnormality.setStatus(AbnormalityStatusEnum.PENDING.getStatus());
        abnormality.setDescription(description);
        
        return abnormality;
    }
    
    /**
     * 转换为基本信息DTO（用户视图）
     */
    private ElevatorBasicInfoDTO convertToBasicInfo(Elevator elevator) {
        ElevatorBasicInfoDTO dto = new ElevatorBasicInfoDTO();
        dto.setId(elevator.getId());
        dto.setElevatorNumber(elevator.getElevatorNumber());
        dto.setInstallationDate(elevator.getInstallationDate());
        dto.setLastMaintenanceDate(elevator.getLastMaintenanceDate());
        dto.setCurrentStatus((String) elevator.getCurrentStatus());
        return dto;
    }
    
    /**
     * 转换为详细信息DTO（管理员视图）
     */
    private ElevatorDetailDTO convertToDetailInfo(Elevator elevator) {
        ElevatorDetailDTO dto = new ElevatorDetailDTO();
        BeanUtils.copyProperties(elevator, dto);
        
        // 获取电梯配置信息
        ElevatorConfigDTO configDTO = elevatorConfigService.getElevatorConfig(elevator.getId());
        dto.setConfig(configDTO);
        
        return dto;
    }

    /**
     * 处理电梯的未关闭异常
     * @param elevatorId 电梯ID
     * @param maintenanceTime 维护时间
     */
    private void resolveOutstandingAbnormalities(Long elevatorId, Date maintenanceTime) {
        List<ElevatorAbnormalityDTO> abnormalities = elevatorAbnormalityService
                .listAbnormalitiesByElevatorId(elevatorId);
        
        // 找出未关闭的异常
        List<ElevatorAbnormalityDTO> outstandingAbnormalities = abnormalities.stream()
                .filter(a -> !AbnormalityStatusEnum.RESOLVED.getStatus().equals(a.getStatus()) &&
                             !AbnormalityStatusEnum.CLOSED.getStatus().equals(a.getStatus()))
                .collect(Collectors.toList());
        
        // 自动关闭这些异常，标记为通过维护已解决
        for (ElevatorAbnormalityDTO abnormality : outstandingAbnormalities) {
            elevatorAbnormalityService.handleAbnormality(
                    abnormality.getId(),
                    null, // 系统自动处理，无处理人
                    AbnormalityStatusEnum.RESOLVED.getStatus(),
                    "电梯已于 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(maintenanceTime) + " 完成维护，异常已解决"
            );
            
            // 记录日志
            System.out.println("电梯ID: " + elevatorId + " 已完成维护，异常ID: " + abnormality.getId() + " 已自动关闭");
        }
        
        // 记录此次维护活动
        if (!outstandingAbnormalities.isEmpty()) {
            System.out.println("电梯ID: " + elevatorId + " 维护完成，共解决 " + outstandingAbnormalities.size() + " 个异常");
        }
    }
}




