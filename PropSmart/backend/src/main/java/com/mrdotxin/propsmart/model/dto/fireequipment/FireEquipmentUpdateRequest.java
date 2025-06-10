package com.mrdotxin.propsmart.model.dto.fireequipment;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新消防设备请求
 */
@Data
@Tag(name = "更新消防设备请求")
public class FireEquipmentUpdateRequest implements Serializable {

    @Schema(description = "设备ID", required = true)
    private Long id;

    @Schema(description = "所属楼栋ID")
    private Long buildingId;

    @Schema(description = "设备所在具体楼层")
    private String specificLevel;

    @Schema(description = "当前状态")
    private String currentStatus;

    @Schema(description = "下次巡检截止时间")
    private Date nextInspectionDue;

    private static final long serialVersionUID = 1L;
} 