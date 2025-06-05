package com.mrdotxin.propsmart.model.dto.repairOrder;

import com.mrdotxin.propsmart.constant.CommonConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "报修单查询请求")
public class RepairOrderQueryRequest implements Serializable {

    @ApiModelProperty(value = "报修单ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "房产ID")
    private Long propertyId;

    @ApiModelProperty(value = "问题描述（模糊查询）")
    private String description;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "处理人ID")
    private Long reviewerId;

    @ApiModelProperty(value = "创建时间开始")
    private Date createStart;

    @ApiModelProperty(value = "创建时间结束")
    private Date createEnd;

    @ApiModelProperty(value = "处理时间开始")
    private Date reviewStart;

    @ApiModelProperty(value = "处理时间结束")
    private Date reviewEnd;

    @ApiModelProperty(value = "当前页号", required = true)
    private long current = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private long pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序顺序（asc/desc）")
    private String sortOrder = CommonConstant.SORT_ORDER_DESC;
}