package com.mrdotxin.propsmart.model.dto.complaint;

import com.mrdotxin.propsmart.common.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询投诉建议请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ComplaintSuggestionQueryRequest extends PageRequest implements Serializable {
    
    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;
    
    /**
     * 类型（complaint/suggestion）
     */
    @Schema(description = "类型（complaint/suggestion）")
    private String type;
    
    /**
     * 状态（pending/success/rejected）
     */
    @Schema(description = "状态（pending/success/rejected）")
    private String status;
    
    /**
     * 创建用户 id
     */
    @Schema(description = "创建用户 id")
    private Long userId;

    /**
     * 处理员 id
     */
    @Schema(description = "处理员 id")
    private Long reviewerId;

    private static final long serialVersionUID = 1L;
} 