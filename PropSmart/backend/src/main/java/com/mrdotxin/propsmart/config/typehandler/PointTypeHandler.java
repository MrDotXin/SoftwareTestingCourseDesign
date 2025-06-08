package com.mrdotxin.propsmart.config.typehandler;

import com.mrdotxin.propsmart.config.typehandler.GeometryTypeHandler;
import org.apache.ibatis.type.MappedTypes;
import org.locationtech.jts.geom.Point;

@MappedTypes(Point.class)
public class PointTypeHandler extends GeometryTypeHandler {

}
