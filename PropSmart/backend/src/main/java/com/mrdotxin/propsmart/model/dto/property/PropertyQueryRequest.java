package com.mrdotxin.propsmart.model.dto.property;

import com.mrdotxin.propsmart.common.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 房产信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PropertyQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户Id
     */
    @Schema(description = "用户Id")
    private Long ownerId;

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


    private static final long serialVersionUID = 1L;
}
