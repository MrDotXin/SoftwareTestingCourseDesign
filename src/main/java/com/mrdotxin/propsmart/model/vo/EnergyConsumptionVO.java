package com.mrdotxin.propsmart.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "能耗记录视图对象")
public class EnergyConsumptionVO {

    @ApiModelProperty(value = "记录ID")
    private Long id;

    @ApiModelProperty(value = "房产ID")
    private Long propertyId;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    @ApiModelProperty(value = "单元号")
    private String unitNumber;

    @ApiModelProperty(value = "房间号")
    private String roomNumber;

    @ApiModelProperty(value = "能耗类型")
    private String energyType;

    @ApiModelProperty(value = "能耗类型文本")
    private String energyTypeText;

    @ApiModelProperty(value = "消耗值")
    private Double consumption;

    @ApiModelProperty(value = "单价")
    private Double price;

    @ApiModelProperty(value = "总费用")
    private Double totalCost;

    @ApiModelProperty(value = "测量时间")
    private Date measureTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public String getEnergyTypeText() {
        switch (this.energyType) {
            case "electricity": return "电力";
            case "water": return "水";
            default: return "未知";
        }
    }
}