package com.mrdotxin.propsmart.model.dto.energyConsumption;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Tag(name = "能耗记录添加请求")
public class EnergyConsumptionAddRequest implements Serializable {

    @Schema(description = "房产ID", required = true)
    private Long propertyId;

    @Schema(description = "能耗类型", required = true)
    private String energyType;

    @Schema(description = "消耗值", required = true)
    private Double consumption;

    @Schema(description = "单价", required = true)
    private Double price;

    @Schema(description = "测量时间", required = true)
    private Date measureTime;
}