package com.mrdotxin.propsmart.model.enums;

/**
 * 异常处理状态枚举
 */
public enum AbnormalityStatusEnum {
    /**
     * 待处理
     */
    PENDING("待处理"),
    
    /**
     * 处理中
     */
    PROCESSING("处理中"),
    
    /**
     * 已解决
     */
    RESOLVED("已解决"),
    
    /**
     * 已关闭
     */
    CLOSED("已关闭");
    
    private final String status;
    
    AbnormalityStatusEnum(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
} 