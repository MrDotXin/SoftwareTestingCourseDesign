package com.mrdotxin.propsmart.model.dto.building;

import com.mrdotxin.propsmart.model.geo.GeoPoint;
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
     * 地理位置几何图形
     */
    @ApiModelProperty(value = "地理位置几何图形")
    private GeoPoint location;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
