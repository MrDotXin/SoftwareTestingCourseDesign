package com.mrdotxin.propsmart.model.dto.fireequipment;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;

/**
 * 消防设备巡检请求
 */
@Data
@Tag(name = "消防设备巡检请求")
public class FireEquipmentInspectionRequest implements Serializable {

    @Schema(description = "设备ID", required = true)
    private Long equipmentId;

    @Schema(description = "巡检备注")
    private String remarks;

    private static final long serialVersionUID = 1L;
} 