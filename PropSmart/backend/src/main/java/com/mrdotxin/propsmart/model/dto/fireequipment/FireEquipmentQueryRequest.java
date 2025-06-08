package com.mrdotxin.propsmart.model.dto.fireequipment;

import com.mrdotxin.propsmart.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 消防设备查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "消防设备查询请求")
public class FireEquipmentQueryRequest extends PageRequest implements Serializable {

    @ApiModelProperty(value = "设备状态")
    private String status;

    @ApiModelProperty(value = "所属楼栋ID")
    private Long buildingId;

    private static final long serialVersionUID = 1L;
} 