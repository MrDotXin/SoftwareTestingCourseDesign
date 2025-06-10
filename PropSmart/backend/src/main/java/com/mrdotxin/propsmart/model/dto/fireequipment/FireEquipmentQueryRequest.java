package com.mrdotxin.propsmart.model.dto.fireequipment;

import com.mrdotxin.propsmart.common.PageRequest;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 消防设备查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Tag(name = "消防设备查询请求")
public class FireEquipmentQueryRequest extends PageRequest implements Serializable {

    @Schema(description = "设备状态")
    private String status;

    @Schema(description = "所属楼栋ID")
    private Long buildingId;

    private static final long serialVersionUID = 1L;
} 