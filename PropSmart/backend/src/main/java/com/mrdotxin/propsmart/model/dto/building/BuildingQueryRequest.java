package com.mrdotxin.propsmart.model.dto.building;

import com.mrdotxin.propsmart.common.PageRequest;
import com.mrdotxin.propsmart.model.geo.GeoPoint;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "主键")
    private Long id;

    /**
     * 楼栋名称/编号
     */
    @Schema(description = "楼栋名称/编号")
    private String buildingName;

    /**
     * 楼栋总层数
     */
    @Schema(description = "楼栋总层数")
    private Integer totalFloors;

    /**
     * 用于地理位置搜索的中心点
     */
    @Schema(description = "搜索中心点")
    private GeoPoint centerPoint;

    /**
     * 搜索半径（米）
     */
    @Schema(description = "搜索半径（米）")
    private Double searchRadius;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}
