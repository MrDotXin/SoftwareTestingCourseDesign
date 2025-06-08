package com.mrdotxin.propsmart.urgent.model.entity;

import lombok.Data;
import org.locationtech.jts.geom.Point;

@Data
public class EvacuationPoint {

    private Point point;

    private String description;
}
