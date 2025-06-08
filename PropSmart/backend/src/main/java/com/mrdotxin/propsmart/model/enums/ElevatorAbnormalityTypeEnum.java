package com.mrdotxin.propsmart.model.enums;

import lombok.Getter;

/**
 * 电梯异常类型枚举
 */
@Getter
public enum ElevatorAbnormalityTypeEnum {
    /**
     * 门故障
     */
    DOOR_FAULT("门故障", "电梯门无法正常开关"),
    
    /**
     * 超载
     */
    OVERLOAD("超载", "电梯负载超过额定值"),
    
    /**
     * 轿厢温度异常
     */
    CABIN_OVERHEATING("轿厢温度异常", "轿厢温度超过安全阈值"),
    
    /**
     * 电机温度异常
     */
    MOTOR_OVERHEATING("电机温度异常", "电机温度超过安全阈值"),
    
    /**
     * 超速
     */
    OVERSPEED("超速", "电梯运行速度超过安全阈值"),
    
    /**
     * 功耗异常
     */
    POWER_ANOMALY("功耗异常", "电梯功耗超过正常范围"),
    
    /**
     * 制动系统故障
     */
    BRAKE_FAILURE("制动系统故障", "电梯制动系统无法正常工作"),
    
    /**
     * 传感器异常
     */
    SENSOR_FAILURE("传感器异常", "电梯传感器数据异常或无响应"),
    
    /**
     * 其他
     */
    OTHER("其他", "未分类的电梯异常");
    
    private final String type;
    private final String description;
    
    ElevatorAbnormalityTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }
} 