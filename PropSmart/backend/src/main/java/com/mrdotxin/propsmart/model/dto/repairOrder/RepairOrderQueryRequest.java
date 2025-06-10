package com.mrdotxin.propsmart.model.dto.repairOrder;

import com.mrdotxin.propsmart.constant.CommonConstant;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Tag(name = "报修单查询请求")
public class RepairOrderQueryRequest implements Serializable {

    @Schema(description = "报修单ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "房产ID")
    private Long propertyId;

    @Schema(description = "问题描述（模糊查询）")
    private String description;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "处理人ID")
    private Long reviewerId;

    @Schema(description = "创建时间开始")
    private Date createStart;

    @Schema(description = "创建时间结束")
    private Date createEnd;

    @Schema(description = "处理时间开始")
    private Date reviewStart;

    @Schema(description = "处理时间结束")
    private Date reviewEnd;

    @Schema(description = "当前页号", required = true)
    private long current = 1;

    @Schema(description = "页面大小", required = true)
    private long pageSize = 10;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序顺序（asc/desc）")
    private String sortOrder = CommonConstant.SORT_ORDER_DESC;
}