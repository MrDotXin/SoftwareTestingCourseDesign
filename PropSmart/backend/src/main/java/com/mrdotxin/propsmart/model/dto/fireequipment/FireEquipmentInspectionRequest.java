package com.mrdotxin.propsmart.model.dto.fireequipment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 消防设备巡检请求
 */
@Data
@ApiModel(description = "消防设备巡检请求")
public class FireEquipmentInspectionRequest implements Serializable {

    @ApiModelProperty(value = "设备ID", required = true)
    private Long equipmentId;

    @ApiModelProperty(value = "巡检备注")
    private String remarks;

    private static final long serialVersionUID = 1L;
} 