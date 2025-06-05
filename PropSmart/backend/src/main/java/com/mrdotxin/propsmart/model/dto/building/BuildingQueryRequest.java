package com.mrdotxin.propsmart.model.dto.building;

import com.mrdotxin.propsmart.common.PageRequest;
import com.mrdotxin.propsmart.model.geo.GeoPoint;
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
     * 楼栋总层数
     */
    @ApiModelProperty(value = "楼栋总层数")
    private Integer totalFloors;

    /**
     * 地理位置
     */
    @ApiModelProperty(value = "地理位置几何图形")
    private GeoPoint location;

    /**
     * 用于地理位置搜索的中心点
     */
    @ApiModelProperty(value = "搜索中心点")
    private GeoPoint centerPoint;

    /**
     * 搜索半径（米）
     */
    @ApiModelProperty(value = "搜索半径（米）")
    private Double searchRadius;

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
