package com.mrdotxin.propsmart.model.enums;

/**
 * 异常级别枚举
 */
public enum AbnormalityLevelEnum {
    /**
     * 轻微
     */
    MINOR("轻微"),
    
    /**
     * 中等
     */
    MODERATE("中等"),
    
    /**
     * 严重
     */
    SEVERE("严重");
    
    private final String level;
    
    AbnormalityLevelEnum(String level) {
        this.level = level;
    }
    
    public String getLevel() {
        return level;
    }
} 