package com.mrdotxin.propsmart.model.dto.energyConsumption;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Tag(name = "能耗记录更新请求")
public class EnergyConsumptionUpdateRequest implements Serializable {

    @Schema(description = "记录ID", required = true)
    private Long id;

    @Schema(description = "房产ID")
    private Long propertyId;

    @Schema(description = "能耗类型")
    private String energyType;

    @Schema(description = "消耗值")
    private Double consumption;

    @Schema(description = "单价")
    private Double price;

    @Schema(description = "测量时间")
    private Date measureTime;
}
