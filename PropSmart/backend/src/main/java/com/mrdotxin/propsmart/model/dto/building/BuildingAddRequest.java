package com.mrdotxin.propsmart.model.dto.building;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BuildingAddRequest implements Serializable {
    /**
     * 楼栋名称/编号
     */
    @ApiModelProperty(value = "楼栋名称/编号")
    private String buildingName;

    /**
     * 地理位置
     */
    @ApiModelProperty(value = "地理位置")
    private String address;

    private static final long serialVersionUID = 1L;
}
