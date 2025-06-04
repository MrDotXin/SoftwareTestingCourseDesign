package com.mrdotxin.propsmart.model.dto.complaint;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建投诉建议请求
 */
@Data
public class ComplaintSuggestionAddRequest implements Serializable {
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 类型（complaint/suggestion）
     */
    private String type;
    
    private static final long serialVersionUID = 1L;
} 