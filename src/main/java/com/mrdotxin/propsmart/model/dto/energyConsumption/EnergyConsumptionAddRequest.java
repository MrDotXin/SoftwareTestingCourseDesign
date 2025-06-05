package com.mrdotxin.propsmart.model.dto.energyConsumption;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "能耗记录添加请求")
public class EnergyConsumptionAddRequest implements Serializable {

    @ApiModelProperty(value = "房产ID", required = true)
    private Long propertyId;

    @ApiModelProperty(value = "能耗类型", required = true)
    private String energyType;

    @ApiModelProperty(value = "消耗值", required = true)
    private Double consumption;

    @ApiModelProperty(value = "单价", required = true)
    private Double price;

    @ApiModelProperty(value = "测量时间", required = true)
    private Date measureTime;
}