package com.mrdotxin.propsmart.model.dto.fireequipment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加消防设备请求
 */
@Data
@ApiModel(description = "添加消防设备请求")
public class FireEquipmentAddRequest implements Serializable {

    @ApiModelProperty(value = "所属楼栋ID", required = true)
    private Long buildingId;

    @ApiModelProperty(value = "设备所在具体楼层", required = true)
    private String specificLevel;

    private static final long serialVersionUID = 1L;
} 