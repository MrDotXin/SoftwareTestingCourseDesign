package com.mrdotxin.propsmart.model.dto.building;

import com.mrdotxin.propsmart.model.geo.GeoPoint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BuildingUpdateRequest implements Serializable {
    /**
     *
     */
    @Schema(description = "id")
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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
