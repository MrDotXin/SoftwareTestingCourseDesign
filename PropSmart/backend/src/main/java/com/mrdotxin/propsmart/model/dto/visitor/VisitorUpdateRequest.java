package com.mrdotxin.propsmart.model.dto.visitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新访客申请请求（管理员审批）
 */
@Data
public class VisitorUpdateRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 审批状态（approved/rejected）
     */
    @ApiModelProperty(value = "审批状态（approved/rejected）")
    private String reviewStatus;

    /**
     * 审批理由
     */
    @ApiModelProperty(value = "审批理由")
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
} 