package com.mrdotxin.propsmart.model.dto.fireequipment;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加消防设备请求
 */
@Data
@Tag(name = "添加消防设备请求")
public class FireEquipmentAddRequest implements Serializable {

    @Schema(description = "所属楼栋ID", required = true)
    private Long buildingId;

    @Schema(description = "设备所在具体楼层", required = true)
    private String specificLevel;

    private static final long serialVersionUID = 1L;
} 