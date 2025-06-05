package com.mrdotxin.propsmart.model.dto.fireequipment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 添加消防设备请求
 */
@Data
@ApiModel(value = "添加消防设备请求")
public class FireEquipmentAddRequest implements Serializable {

    /**
     * 所属楼栋ID
     */
    @ApiModelProperty(value = "所属楼栋ID")
    private Long buildingId;

    /**
     * 当前状态
     */
    @ApiModelProperty(value = "当前状态")
    private String currentStatus;

    /**
     * 上次巡检人ID
     */
    @ApiModelProperty(value = "上次巡检人ID")
    private Long lastInspectorId;

    /**
     * 上次巡检时间
     */
    @ApiModelProperty(value = "上次巡检时间")
    private Date lastInspectionTime;

    /**
     * 下次巡检截止时间
     */
    @ApiModelProperty(value = "下次巡检截止时间")
    private Date nextInspectionDue;

    /**
     * 巡检备注
     */
    @ApiModelProperty(value = "巡检备注")
    private String inspectionRemarks;

    private static final long serialVersionUID = 1L;
} 