package com.mrdotxin.propsmart.model.geo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mrdotxin.propsmart.config.typehandler.PointTypeHandler;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import javax.persistence.Table;

@Data
@TableName("road_nodes_v2")
public class RoadNode {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(typeHandler = PointTypeHandler.class)
    private Point geom;
}
