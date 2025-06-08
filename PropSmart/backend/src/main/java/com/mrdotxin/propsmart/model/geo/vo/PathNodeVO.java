package com.mrdotxin.propsmart.model.geo.vo;

import lombok.Data;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

@Data
public class PathNodeVO {
    private Integer seq;
    private Long node;
    private Long edge;
    private Double cost;
    private Double aggCost;
    private Point point;
}
