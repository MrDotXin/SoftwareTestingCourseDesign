package com.mrdotxin.propsmart.urgent.model.entity;

import lombok.Data;
import org.locationtech.jts.geom.Point;

@Data
public class EmergencyLocation {

    Point point;

    String locationDesc;

    Double affectRange;
}
