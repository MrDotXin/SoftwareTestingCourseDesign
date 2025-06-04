package com.mrdotxin.propsmart.model.dto.complaint;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新投诉建议请求（管理员审批）
 */
@Data
public class ComplaintSuggestionUpdateRequest implements Serializable {
    
    /**
     * id（管理员）
     */
    private Long id;
    
    /**
     * 状态（success/rejected）
     */
    private String status;
    
    /**
     * 回复内容
     */
    private String reviewMessage;
    
    private static final long serialVersionUID = 1L;
} 