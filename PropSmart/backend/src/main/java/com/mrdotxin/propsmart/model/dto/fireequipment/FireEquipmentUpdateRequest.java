package com.mrdotxin.propsmart.model.dto.fireequipment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新消防设备请求
 */
@Data
@ApiModel(description = "更新消防设备请求")
public class FireEquipmentUpdateRequest implements Serializable {

    @ApiModelProperty(value = "设备ID", required = true)
    private Long id;

    @ApiModelProperty(value = "所属楼栋ID")
    private Long buildingId;

    @ApiModelProperty(value = "设备所在具体楼层")
    private String specificLevel;

    @ApiModelProperty(value = "当前状态")
    private String currentStatus;

    @ApiModelProperty(value = "下次巡检截止时间")
    private Date nextInspectionDue;

    private static final long serialVersionUID = 1L;
} 