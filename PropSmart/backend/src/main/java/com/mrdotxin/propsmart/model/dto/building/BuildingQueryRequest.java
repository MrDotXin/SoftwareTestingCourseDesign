package com.mrdotxin.propsmart.model.dto.building;

import com.mrdotxin.propsmart.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class BuildingQueryRequest extends PageRequest implements Serializable {
    /**
     *
     */
    @ApiModelProperty(value = "主键")
    private Long id;

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

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}
