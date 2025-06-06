package com.mrdotxin.propsmart.model.dto.energyConsumption;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "能耗记录更新请求")
public class EnergyConsumptionUpdateRequest implements Serializable {

    @ApiModelProperty(value = "记录ID", required = true)
    private Long id;

    @ApiModelProperty(value = "房产ID")
    private Long propertyId;

    @ApiModelProperty(value = "能耗类型")
    private String energyType;

    @ApiModelProperty(value = "消耗值")
    private Double consumption;

    @ApiModelProperty(value = "单价")
    private Double price;

    @ApiModelProperty(value = "测量时间")
    private Date measureTime;
}
