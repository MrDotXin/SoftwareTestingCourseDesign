package com.mrdotxin.propsmart.model.enums;

/**
 * 电梯运行方向枚举
 */
public enum ElevatorDirectionEnum {
    /**
     * 上行
     */
    UP("上行"),
    
    /**
     * 下行
     */
    DOWN("下行"),
    
    /**
     * 静止
     */
    STATIONARY("静止");
    
    private final String direction;
    
    ElevatorDirectionEnum(String direction) {
        this.direction = direction;
    }
    
    public String getDirection() {
        return direction;
    }
}
