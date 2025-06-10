package com.mrdotxin.propsmart.model.dto.property;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class PropertyUpdateRequest implements Serializable {

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 实际拥有者的身份证号
     */
    @Schema(description = "实际拥有者的身份证号")
    private Long ownerIdentity;

    /**
     * 楼栋名称
     */
    @Schema(description = "楼栋名称")
    private String buildingName;

    /**
     * 单元号
     */
    @Schema(description = "单元号")
    private String unitNumber;

    /**
     * 房号
     */
    @Schema(description = "房号")
    private String roomNumber;

    /**
     * 面积
     */
    @Schema(description = "面积")
    private Double area;


    private static final long serialVersionUID = 1L;
}

