package com.mrdotxin.propsmart.model.dto.building;

import com.mrdotxin.propsmart.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BuildingUpdateRequest implements Serializable {
    /**
     *
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 楼栋名称/编号
     */
    @ApiModelProperty(value = "楼栋名称/编号")
    private String buildingName;

    /**
     * 楼栋总层数
     */
    @ApiModelProperty(value = "楼栋总层数")
    private Integer totalFloors;

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

    private static final long serialVersionUID = 1L;
}
