package com.mrdotxin.propsmart.model.enums;

import lombok.Getter;

/**
 * 异常级别枚举
 */
@Getter
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
    SERIOUS("严重");
    
    private final String level;
    
    AbnormalityLevelEnum(String level) {
        this.level = level;
    }

} 