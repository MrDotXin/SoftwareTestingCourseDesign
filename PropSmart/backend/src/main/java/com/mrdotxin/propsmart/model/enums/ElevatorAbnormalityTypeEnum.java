package com.mrdotxin.propsmart.model.enums;

/**
 * 电梯异常类型枚举
 */
public enum ElevatorAbnormalityTypeEnum {
    /**
     * 门故障
     */
    DOOR_FAULT("门故障"),
    
    /**
     * 超载
     */
    OVERLOAD("超载"),
    
    /**
     * 温度异常
     */
    TEMPERATURE_ABNORMAL("温度异常"),
    
    /**
     * 速度异常
     */
    SPEED_ABNORMAL("速度异常"),
    
    /**
     * 加速度异常
     */
    ACCELERATION_ABNORMAL("加速度异常"),
    
    /**
     * 停电
     */
    POWER_OUTAGE("停电"),
    
    /**
     * 传感器异常
     */
    SENSOR_ABNORMAL("传感器异常"),
    
    /**
     * 其他
     */
    OTHER("其他");
    
    private final String type;
    
    ElevatorAbnormalityTypeEnum(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
} 