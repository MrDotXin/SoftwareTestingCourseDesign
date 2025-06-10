package com.mrdotxin.propsmart.model.dto.energyConsumption;

import com.mrdotxin.propsmart.constant.CommonConstant;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Tag(name = "能耗记录查询请求")
public class EnergyConsumptionQueryRequest implements Serializable {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "房产ID")
    private Long propertyId;

    @Schema(description = "能耗类型")
    private String energyType;

    @Schema(description = "最小消耗值")
    private Double minConsumption;

    @Schema(description = "最大消耗值")
    private Double maxConsumption;

    @Schema(description = "最小价格")
    private Double minPrice;

    @Schema(description = "最大价格")
    private Double maxPrice;

    @Schema(description = "测量时间开始")
    private Date measureStart;

    @Schema(description = "测量时间结束")
    private Date measureEnd;

    @Schema(description = "创建时间开始")
    private Date createStart;

    @Schema(description = "创建时间结束")
    private Date createEnd;

    @Schema(description = "当前页号", required = true)
    private long current = 1;

    @Schema(description = "页面大小", required = true)
    private long pageSize = 10;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序顺序（asc/desc）")
    private String sortOrder = CommonConstant.SORT_ORDER_DESC;
}