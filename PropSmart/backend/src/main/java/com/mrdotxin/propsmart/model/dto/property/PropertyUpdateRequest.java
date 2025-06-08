package com.mrdotxin.propsmart.model.dto.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PropertyUpdateRequest implements Serializable {

    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 实际拥有者的身份证号
     */
    @ApiModelProperty(value = "实际拥有者的身份证号")
    private Long ownerIdentity;

    /**
     * 楼栋名称
     */
    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    /**
     * 单元号
     */
    @ApiModelProperty(value = "单元号")
    private String unitNumber;

    /**
     * 房号
     */
    @ApiModelProperty(value = "房号")
    private String roomNumber;

    /**
     * 面积
     */
    @ApiModelProperty(value = "面积")
    private Double area;


    private static final long serialVersionUID = 1L;
}

