package com.mrdotxin.propsmart.model.dto.complaint;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 创建投诉建议请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintSuggestionAddRequest implements Serializable {
    
    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;
    
    /**
     * 类型（complaint/suggestion）
     */
    @Schema(description = "类型")
    private String type;
    
    private static final long serialVersionUID = 1L;
} 