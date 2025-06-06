package com.mrdotxin.propsmart.model.enums;

import lombok.Getter;

/**
 * 电梯运行状态枚举
 */
@Getter
public enum ElevatorStatusEnum {
    /**
     * 正常
     */
    NORMAL("正常"),
    
    /**
     * 预警
     */
    WARNING("预警"),
    
    /**
     * 故障
     */
    FAULT("故障"),
    
    /**
     * 维护中
     */
    MAINTENANCE("维护中");
    
    private final String status;
    
    ElevatorStatusEnum(String status) {
        this.status = status;
    }

}

