package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mrdotxin.propsmart.config.typehandler.MysqlGeoTypeHandler;
import com.mrdotxin.propsmart.model.geo.GeoPoint;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 楼栋信息
 *
 */
@TableName(value = "building")
@Data
public class Building implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 楼栋名称/编号
     */
    @TableField(value = "buildingName")
    private String buildingName;

    /**
     * 楼栋总层数
     */
    @TableField(value = "totalLevels")
    private Integer totalLevels;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}