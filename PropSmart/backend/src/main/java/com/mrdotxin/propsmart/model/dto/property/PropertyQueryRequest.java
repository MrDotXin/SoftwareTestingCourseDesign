package com.mrdotxin.propsmart.model.dto.property;

import com.mrdotxin.propsmart.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "用户Id")
    private Long ownerId;

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


    private static final long serialVersionUID = 1L;
}
