package com.mrdotxin.propsmart.model.dto.building;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BuildingAddRequest implements Serializable {
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
