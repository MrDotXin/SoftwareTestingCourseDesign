package com.mrdotxin.propsmart.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "月度能耗统计视图对象")
public class EnergyMonthlyStatsVO {

    @ApiModelProperty(value = "日期（日）")
    private Integer day;

    @ApiModelProperty(value = "总消耗量")
    private Double totalConsumption;

    @ApiModelProperty(value = "总费用")
    private Double totalCost;
}
