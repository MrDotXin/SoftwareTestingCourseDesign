package com.mrdotxin.propsmart.model.geo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mrdotxin.propsmart.config.typehandler.LineStringTypeHandler;
import lombok.Data;
import org.locationtech.jts.geom.LineString;

@Data
@TableName("road_network")
public class RoadSegment {

    @TableId(type = IdType.ASSIGN_ID)
    private Long osmId;

    @TableField("name")
    private String name;

    @TableField(typeHandler = LineStringTypeHandler.class)
    private LineString way;

    @TableField("cost")
    private Double cost;

    @TableField("source")
    private Long source;

    @TableField("target")
    private Long target;
}
