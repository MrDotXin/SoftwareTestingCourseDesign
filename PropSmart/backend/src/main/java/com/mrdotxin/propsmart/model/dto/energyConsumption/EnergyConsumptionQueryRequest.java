package com.mrdotxin.propsmart.model.dto.energyConsumption;

import com.mrdotxin.propsmart.constant.CommonConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "能耗记录查询请求")
public class EnergyConsumptionQueryRequest implements Serializable {

    @ApiModelProperty(value = "记录ID")
    private Long id;

    @ApiModelProperty(value = "房产ID")
    private Long propertyId;

    @ApiModelProperty(value = "能耗类型")
    private String energyType;

    @ApiModelProperty(value = "最小消耗值")
    private Double minConsumption;

    @ApiModelProperty(value = "最大消耗值")
    private Double maxConsumption;

    @ApiModelProperty(value = "最小价格")
    private Double minPrice;

    @ApiModelProperty(value = "最大价格")
    private Double maxPrice;

    @ApiModelProperty(value = "测量时间开始")
    private Date measureStart;

    @ApiModelProperty(value = "测量时间结束")
    private Date measureEnd;

    @ApiModelProperty(value = "创建时间开始")
    private Date createStart;

    @ApiModelProperty(value = "创建时间结束")
    private Date createEnd;

    @ApiModelProperty(value = "当前页号", required = true)
    private long current = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private long pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序顺序（asc/desc）")
    private String sortOrder = CommonConstant.SORT_ORDER_DESC;
}